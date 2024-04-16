package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static java.util.Calendar.DECEMBER;

@Component
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final LocalDate movieBirthday = LocalDate.of(1895, DECEMBER, 28);

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> findAll() {
        String sqlQuery = "SELECT film_id, film_name, description, release_date, duration, rating_id FROM films";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film findFilmById(Integer filmId) {
        String sqlQuery = "SELECT * FROM films WHERE film_id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery, filmId);
        if (filmRows.next()) {
            log.info("Найден фильм: {} {}", filmRows.getString("film_id"), filmRows.getString("film_name"));
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, filmId);
        } else {
            log.info("Фильм с идентификатором {} не найден.", filmId);
            throw new FilmNotFoundException("Фильм с идентификатором " + filmId + "не найден.");
        }
    }

    @Override
    public Film create(Film film) {
        if (film.getReleaseDate().isBefore(movieBirthday)) {
            log.debug("Валидация не пройдена: дата релиза ранее дня рождения кино");
            throw new ValidationException("Дата релиза фильма должна быть не раньше 28 декабря 1895 года.");
        }
        if (film.getMpa().getId() > 5) {
            log.debug("Валидация не пройдена: рейтинг не сущуствует");
            throw new ValidationException("Рейтинг с id " + film.getMpa().getId() + " не существует.");
        }
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                Integer id = genre.getId();
                    if (id > 6) {
                    log.debug("Валидация не пройдена: жанр не существует");
                    throw new ValidationException("Жанр с id " + id + " не существует.");
                }
            }
        }
        Integer id = saveAndReturnId(film);
        Set<Genre> genres = film.getGenres();
        addGenre(id, genres);
        film.setId(id);
        log.info("Фильм " + film + " успешно создан");
        return film;
    }

    @Override
    public Film put(Film film) {
        if (film.getId() == null) {
            String sqlQuery = "UPDATE films SET " +
                    "film_name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?" +
                    "WHERE film_id = ?";
            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    null);
            log.info("Фильм (id = " + film.getId() + ") успешно обновлён");
        } else {
            findFilmById(film.getId());
            String sqlQuery = "UPDATE films SET " +
                    "film_name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?" +
                    "WHERE film_id = ?";
            jdbcTemplate.update(sqlQuery,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId(),
                    film.getId());
            log.info("Фильм (id = " + film.getId() + ") успешно обновлён");
        }
        return film;
    }

    @Override
    public void delete(Film film) {
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        log.info("Фильм (id = " + film.getId() + ") успешно удалён");
    }

    @Override
    public void addLike(Integer id, Integer userId) {
        String sqlQuery = "INSERT INTO film_user(film_id, user_id) "
                    + "VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId);
        log.info("Добавлен лайк (id пользователя = " + userId + " , id фильма = " + id);
    }

    @Override
    public void deleteLike(Integer id, Integer userId) {
        String sqlQuery = "DELETE FROM film_user WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sqlQuery, userId, id);
        log.info("Удалён лайк (id пользователя = " + userId + " , id фильма = " + id);
    }

    @Override
    public List<Film> displayPopularFilms(Integer count) {
        String sqlQuery = "SELECT * FROM films "
                + "LEFT JOIN (SELECT film_id, COUNT (film_user.user_id) as count from film_user "
                + "GROUP BY film_id) AS fu ON fu.film_id = films.film_id "
                + "GROUP BY films.film_id "
                + "ORDER BY count DESC "
                + "LIMIT "
                + count;
        log.debug("Сформирован список " + count + " наиболее популярных фильмов.");
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Map<Integer, Film> getFilms() {
        return null;
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM genres";
        log.debug("Сформирован список всех жанров.");
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Genre findGenreById(Integer id) {
        String sqlQuery = "SELECT * FROM genres WHERE genre_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRows.next()) {
            log.info("Жанр с идентификатором {} найден.", id);
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenre, id);
        } else {
            log.info("Жанр с идентификатором {} не найден.", id);
            throw new GenreNotFoundException("Жанр с идентификатором " + id + "не найден.");
        }
    }

    @Override
    public List<Mpa> getAllRatings() {
        String sqlQuery = "SELECT * FROM rating";
        log.debug("Сформирован список всех рейтингов.");
        return jdbcTemplate.query(sqlQuery, this::mapRowToRating);
    }

    @Override
    public Mpa findRatingById(Integer id) {
        String sqlQuery = "SELECT * FROM rating WHERE rating_id = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (mpaRows.next()) {
            log.info("Рейтинг с идентификатором {} найден.", id);
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToRating, id);
        } else {
            log.info("Рейтинг с идентификатором {} не найден.", id);
            throw new RatingNotFoundException("Рейтинг с идентификатором " + id + "не найден.");
        }

    }

    private Integer saveAndReturnId(Film film) {
        String sqlQuery = "INSERT INTO films(film_name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        HashSet<Genre> genres = new HashSet<>();
        genres.addAll(getGenres(resultSet.getInt("FILM_ID")));
        Film film = Film.builder()
                .id(resultSet.getInt("FILM_ID"))
                .name(resultSet.getString("FILM_NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getLong("DURATION"))
                .genres(genres)
                .mpa(getRatingOrNull(resultSet.getInt("RATING_ID")))
                .build();
        return film;
    }

    private Mpa getRatingOrNull(Integer id) {
        String sqlQuery = "SELECT * FROM rating WHERE rating_id = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (mpaRows.next()) {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToRating, id);
        } else {
            return null;
        }
    }

    private void addGenre(int filmId, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }
        for (Genre genre: genres) {
            String sqlQuery = "INSERT INTO film_genre (film_id, genre_id) "
                    + "VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery, filmId, genre.getId());
        }
        log.info("Жанры к фильму с id=" + filmId + " успешно добавлены.");
    }

    private Set<Genre> getGenres(int filmId) {
        Set<Genre> genres = new HashSet<Genre>();
        String sqlQuery = "SELECT * FROM genres WHERE genre_id IN (SELECT genre_id FROM film_genre " +
                "WHERE film_id = ?)";

        genres.addAll(jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId));
        return genres;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("GENRE_NAME"))
                .build();
    }

    private Mpa mapRowToRating(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("RATING_ID"))
                .name(resultSet.getString("RATING_NAME"))
                .build();
    }

}