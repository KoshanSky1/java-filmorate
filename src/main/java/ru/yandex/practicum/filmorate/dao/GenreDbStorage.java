package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

@Slf4j
@Repository("GenreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenre(int idGenre) {
        String sqlGenre = "select * from G01_GENRE where G01_ID = ?";
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(sqlGenre, (rs, rowNum) -> makeGenre(rs), idGenre);
        } catch (EmptyResultDataAccessException e) {
            throw new GenreNotFoundException(format("Genre [%s] not found in DB", idGenre));
        }
        return genre;
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlAllGenres = "select * from G01_GENRE";
        return jdbcTemplate.query(sqlAllGenres, (rs, rowNum) -> makeGenre(rs));
    }


    @Override
    public Genre createGenre(Genre genre) {
        String sqlQuery = "insert into G01_GENRE " +
                "(G01_NAME) " +
                "values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, genre.getName());

            return preparedStatement;
        }, keyHolder);

        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();

        return getGenre(id);
    }

    @Override
    public Genre updateGenre(Genre genre) {
        try {
            String sqlGenre = "update G01_GENRE set " +
                    "G01_NAME = ? " +
                    "where G01_ID = ?";
            jdbcTemplate.update(sqlGenre,
                    genre.getName(), genre.getId());
        } catch (EmptyResultDataAccessException e) {
            throw new GenreNotFoundException(format("Genre [%s] not found in DB", genre.getId()));
        }
        return getGenre(genre.getId());
    }


    @Override
    public boolean deleteGenre(int idGenre) {
        String sqlQuery = "delete from G01_GENRE where G01_ID = ?";
        return jdbcTemplate.update(sqlQuery, idGenre) > 0;
    }


    @Override
    public List<Genre> getGenresListForFilm(int idFilm) {
        String sql =
                "select fg.*, g.G01_NAME " +
                        "from F02_FILM_GENRE as fg " +
                        "join G01_GENRE as g " +
                        "on g.G01_ID = fg.G01_ID " +
                        "where fg.F01_ID = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), idFilm);
    }


    private Genre makeGenre(ResultSet resultSet) throws SQLException {
        int idGenre = resultSet.getInt("G01_ID");
        return new Genre(
                idGenre,
                resultSet.getString("G01_NAME"));
    }
}
