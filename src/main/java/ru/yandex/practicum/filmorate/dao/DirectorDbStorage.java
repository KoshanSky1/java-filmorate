package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static java.lang.String.format;

@Slf4j
@Repository("DirectorDbStorage")
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;

    @Override
    public List<Director> getAllDirectors() {
        String sqlAllDirectors = "select * from D01_DIRECTOR";
        return jdbcTemplate.query(sqlAllDirectors, this::mapRowToDirector);
    }

    @Override
    public Director getDirectorById(int idDirector) {
        String sql =
                "select * " +
                        "from D01_DIRECTOR " +
                        "where D01_ID = ?";

        Director director;
        try {
            director = jdbcTemplate.queryForObject(sql, this::mapRowToDirector, idDirector);
        } catch (EmptyResultDataAccessException e) {
            throw new DirectorNotFoundException(format("Director [%s] not found in DB", idDirector));
        }

        return director;
    }

    @Override
    public Director addDirectorToDatabase(Director director) {
        Integer id = saveAndReturnDirectorId(director);
        director.setId(id);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sql =
                "update D01_DIRECTOR set " +
                        "D01_NAME = ? " +
                        "where D01_ID = ?";
        if (getDirectorById(director.getId()) == null) {
            throw new DirectorNotFoundException(format("Director [%s] not found in DB", director.getId()));
        }
        jdbcTemplate.update(sql,
                director.getName(), director.getId());

        return director;
    }

    @Override
    public boolean removeDirectorFromDatabase(int idDirector) {
        String sql = "DELETE FROM F05_FILM_DIRECTOR WHERE D01_ID = ?";
        jdbcTemplate.update(sql, idDirector);
        String sqlQuery = "DELETE FROM D01_DIRECTOR WHERE D01_ID = ?";
        return jdbcTemplate.update(sqlQuery, idDirector) > 0;
    }


    public void addDirectorToFilm(Integer idFilm, List<Director> directors) {
        String sql = "INSERT INTO F05_FILM_DIRECTOR(F01_ID, D01_ID) "
                + "VALUES (?, ?)";

        if (directors == null || directors.isEmpty()) {
            return;
        }
        for (Director director: directors) {
            try {
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setInt(1, idFilm);
                    preparedStatement.setInt(2, director.getId());

                    return preparedStatement;
                }, keyHolder);
            } catch (Exception ex) {
                throw new ValidationException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }
    }

    public void removeDirectorFromFilm(Integer idFilm) {
        String sqlQuery = "DELETE FROM F05_FILM_DIRECTOR WHERE F01_ID = ? ";
        jdbcTemplate.update(sqlQuery, idFilm);
    }

    @Override
    public List<Film> searchFilmsByDirector(Integer idDirector) {
        String sqlQuery = "select f.*, "
                + "from F05_FILM_DIRECTOR AS fd "
                + "join F01_FILM AS f on f.F01_ID = fd.F01_ID "
                + "where fd.D01_ID = ? ";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), idDirector);
    }


    public List<Film> searchFilmsByDirectorSortedByYear(Integer idDirector) {
        String sqlQuery = "select f.*, "
                + "from F05_FILM_DIRECTOR AS fd "
                + "join F01_FILM AS f on f.F01_ID = fd.F01_ID "
                + "where fd.D01_ID = ? "
                + "order by f.F01_RELEASE_DATE ";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), idDirector);
    }

    public List<Film> searchFilmsByDirectorSortedByLikes(Integer idDirector) {
        getDirectorById(idDirector);

        String sqlQuery = "SELECT * FROM F01_FILM "
                + "JOIN F05_FILM_DIRECTOR AS fd ON fd.F01_ID = F01_FILM.F01_ID "
                + "LEFT JOIN (SELECT F01_ID, COUNT (L01_LIKES_FILM.U01_ID) as count from L01_LIKES_FILM "
                + "GROUP BY F01_ID) AS lf ON lf.F01_ID = F01_FILM.F01_ID "
                + "WHERE fd.D01_ID = ? "
                + "GROUP BY F01_FILM.F01_ID "
                + "ORDER BY count DESC ";

         return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), idDirector);
    }

    public List<Director> getFilmDirector(int idFilm) {
        String sql = "select d.* " +
                "from F05_FILM_DIRECTOR AS fd " +
                "join D01_DIRECTOR AS d on d.D01_ID = fd.D01_ID " +
                "where F01_ID = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), idFilm);
    }


    private Director makeDirector(ResultSet resultSet) throws SQLException {
        return new  Director(resultSet.getInt("D01_ID"), resultSet.getString("D01_NAME"));
    }

    private Integer saveAndReturnDirectorId(Director director) {
        String sqlQuery = "INSERT INTO D01_DIRECTOR(D01_NAME) "
                + "VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"D01_ID"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).intValue();
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
                getFilmDirector(idFilm));
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return new  Director(resultSet.getInt("D01_ID"), resultSet.getString("D01_NAME"));
    }
}