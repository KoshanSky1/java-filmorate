package ru.yandex.practicum.filmorate.dao;

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
        } catch (Exception ex) {
            throw new ValidationException(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        }

        return jdbcTemplate.update(sqlQuery, idFilm) > 0;
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
                genreDbStorage.getGenresListForFilm(resultSet.getInt("F01_ID")));
    }
}
