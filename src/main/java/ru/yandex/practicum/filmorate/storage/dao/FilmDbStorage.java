package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

@Repository("FilmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final FilmGenreDbStorage filmGenreDbStorage;
    private final DirectorDbStorage directorDbStorage;

    @Override
    public Film createFilm(Film film) {
        String sql =
                "insert into F01_FILM " +
                        "(F01_NAME, F01_DESCRIPTION, F01_RELEASE_DATE, F01_DURATION, M01_ID) " +
                        "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        java.sql.Date sqlDate = Date.valueOf(film.getReleaseDate());

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, film.getName());
                stmt.setString(2, film.getDescription());
                stmt.setDate(3, sqlDate);
                stmt.setInt(4, film.getDuration());
                stmt.setInt(5, film.getMpa().getId());
                return stmt;
            }, keyHolder);
        } catch (Exception ex) {
            throw new ValidationException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();

        List<Genre> filmGenres = filmGenreDbStorage.createFilmGenre(id, film.getGenres());
        film.setGenres(filmGenres);

        directorDbStorage.addDirectorToFilm(id, film.getDirectors());
        film.setDirectors(directorDbStorage.getFilmDirector(id));

        return getFilm(id);
    }

    @Override
    public Film getFilm(int idFilm) {
        String sql =
                "select * " +
                        "from F01_FILM " +
                        "where F01_ID = ?";

        Film film;
        try {
            film = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), idFilm);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException(format("Film [%s] not found in DB", idFilm));
        }

        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql =
                "select * " +
                        "from F01_FILM";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film updateFilm(Film film) {
        String sql =
                "update F01_FILM set " +
                        "F01_NAME = ?, F01_DESCRIPTION = ?, " +
                        "F01_RELEASE_DATE = ?, F01_DURATION = ?, M01_ID = ? " +
                        "where F01_ID = ?";

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        filmGenreDbStorage.deleteFilmGenre(film.getId());
        List<Genre> filmGenres = filmGenreDbStorage.createFilmGenre(film.getId(), film.getGenres());
        film.setGenres(filmGenres);

        directorDbStorage.removeDirectorFromFilm(film.getId());
        directorDbStorage.addDirectorToFilm(film.getId(), film.getDirectors());
        film.setDirectors(directorDbStorage.getFilmDirector(film.getId()));

        return getFilm(film.getId());
    }


    @Override
    public boolean deleteFilm(int idFilm) {
        String sqlQuery =
                "delete " +
                        "from F01_FILM " +
                        "where F01_ID = ?";
        try {
            filmGenreDbStorage.deleteFilmGenre(idFilm);
            directorDbStorage.removeDirectorFromFilm(idFilm);
        } catch (Exception ex) {
            throw new ValidationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        }

        return jdbcTemplate.update(sqlQuery, idFilm) > 0;
    }


    /**
     * Описание запроса:
     * Подзапрос (1) - Найти id пользователей, с максимальным количеством пересечения по лайкам:
     * Объединям таблицу L01_LIKES_FILM с самой собой, делаем right join
     * и оставляем справа только id данного пользователя, слева id всех пользователей, кроме нашего и null.
     * Получается такая таблица: lf1.U01_ID | F01_ID | lf2.U01_ID
     * Таким образом в lf1.U01_ID получаются id всех пользователей, которые лайкали такие же фильмы, что и данный.
     * Группируем пользователей по id, сортируем по частоте этих id
     * (то есть в начале списка будут пользователи, у которых наиболее совпадают лайки с данным).
     * Выбираем первых трех из этих пользователей
     * <p>
     * Подзапрос (2):
     * Находим id фильмов, которые лайкнули найденные пользователи
     * <p>
     * Подзапрос (3):
     * Находим id фильмов, которые лайкнул данный пользователь
     * <p>
     * Запрос (4):
     * Находим фильмы c id, которые есть в списке (2), но нет в списке (3).
     * (То есть те, которые лайкали найденные пользователи, но не лайкал данный)
     */
    @Override
    public List<Film> getRecommendations(int idUser) {
        String sql =
                "select * from F01_FILM f " + //(4)
                        "join M01_MPA m on f.M01_ID = m.M01_ID " +
                        "where f.F01_ID in (" +
                        "select F01_ID from L01_LIKES_FILM " + //(2)
                        "where U01_ID in (" +
                        "select lf1.U01_ID from L01_LIKES_FILM lf1 " + //(1)
                        "right join  L01_LIKES_FILM lf2 on lf2.F01_ID = lf1.F01_ID " +
                        "group by lf1.U01_ID, lf2.U01_ID " +
                        "having lf1.U01_ID is not null and " +
                        "lf1.U01_ID != ? and " +
                        "lf2.U01_ID = ? " +
                        "order by count(lf1.U01_ID) desc " +
                        "limit 3 " +
                        ") " +
                        "and F01_ID not in ( " +
                        "select F01_ID from L01_LIKES_FILM " + //(3)
                        "where U01_ID = ? " +
                        ")" +
                        ")";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), idUser, idUser, idUser);
    }

    @Override
    public List<Film> getCommonFilms(int idUser, int idFriend) {
        String sql =
                "select f.* from F01_FILM as f inner join L01_LIKES_FILM as l" +
                        " on l.F01_ID = f.F01_ID where l.F01_ID in " +
                        "(select F01_ID from L01_LIKES_FILM " +
                        "where U01_ID = ? or U01_ID = ? group by F01_ID having count(*) > 1)" +
                        "group by l.F01_ID order by count(*) desc";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), idUser, idFriend);
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        String[] s = by.split(",");

        String sql;
        query = "%".concat(query).concat("%");
        if (s.length == 2) {
            sql = "SELECT f.* FROM F01_FILM as f " +
                    "LEFT JOIN L01_LIKES_FILM AS l ON l.F01_ID = f.F01_ID " +
                    "LEFT JOIN F05_FILM_DIRECTOR AS fd ON fd.F01_ID = f.F01_ID" +
                    " LEFT JOIN D01_DIRECTOR AS d ON d.D01_ID = fd.D01_ID " +
                    "WHERE lower(d.D01_NAME) LIKE lower(?) OR lower(f.F01_NAME) LIKE lower(?) " +
                    "GROUP BY f.F01_ID " +
                    "ORDER BY COUNT(l.U01_ID) DESC";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), query, query);
        } else if (s[0].equals("director")) {
            sql = "SELECT f.* FROM F01_FILM as f " +
                    "LEFT JOIN L01_LIKES_FILM AS l ON l.F01_ID = f.F01_ID " +
                    "LEFT JOIN F05_FILM_DIRECTOR AS fd ON fd.F01_ID = f.F01_ID" +
                    " LEFT JOIN D01_DIRECTOR AS d ON d.D01_ID = fd.D01_ID " +
                    "WHERE lower(d.D01_NAME) LIKE lower(?) " +
                    "GROUP BY f.F01_ID " +
                    "ORDER BY COUNT(l.U01_ID) DESC";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), query);
        } else {
            sql = "SELECT f.* FROM F01_FILM as f " +
                    "LEFT JOIN L01_LIKES_FILM AS l ON l.F01_ID = f.F01_ID " +
                    "WHERE lower(f.F01_NAME) LIKE lower(?) " +
                    "GROUP BY f.F01_ID " +
                    "ORDER BY COUNT(l.U01_ID) DESC";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), query);
        }
    }

    private Film makeFilm(ResultSet resultSet) throws SQLException {
        int idFilm = resultSet.getInt("F01_ID");
        return new Film(
                idFilm,
                resultSet.getString("F01_NAME"),
                resultSet.getString("F01_DESCRIPTION"),
                Objects.requireNonNull(resultSet.getDate("F01_RELEASE_DATE")).toLocalDate(),
                resultSet.getInt("F01_DURATION"),
                mpaDbStorage.getMpa(resultSet.getInt("M01_ID")),
                genreDbStorage.getGenresListForFilm(resultSet.getInt("F01_ID")),
                directorDbStorage.getFilmDirector(resultSet.getInt("F01_ID")));
    }
}
