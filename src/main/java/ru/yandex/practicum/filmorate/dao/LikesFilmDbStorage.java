package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.lang.Nullable;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.feed.Operation;
import ru.yandex.practicum.filmorate.storage.LikesFilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository("LikesFilmDbStorage")
@RequiredArgsConstructor
public class LikesFilmDbStorage implements LikesFilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final FeedDbStorage feedDbStorage;
    private final DirectorDbStorage directorDbStorage;
    private final UserDbStorage userDbStorage;

    @Override
    public void addLike(int idFilm, int idUser) {
        String sql =
                "insert into L01_LIKES_FILM " +
                        "(F01_ID, U01_ID) " +
                        "values (?, ?) ";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, idFilm);
            preparedStatement.setInt(2, idUser);

            return preparedStatement;
        }, keyHolder);
        feedDbStorage.addEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(idUser)
                .entityId(idFilm)
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .build());
    }


    @Override
    public void deleteLike(int idFilm, int idUser) {
        userDbStorage.getUser(idUser);
        String sql =
                "delete from L01_LIKES_FILM " +
                        "where F01_ID = ? and U01_ID = ? ";

        jdbcTemplate.update(sql, idFilm, idUser);
        feedDbStorage.addEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(idUser)
                .entityId(idFilm)
                .eventType(EventType.LIKE)
                .operation(Operation.REMOVE)
                .build());
    }

    @Override
    public List<Film> getPopularFilms(Integer count, @Nullable Integer genreId, @Nullable Integer year) {
        List<String> params = new ArrayList<>();
        if (count == null) {
            count = 10;
        }

        if (genreId != null) params.add(String.format("fg.G01_ID = %s", genreId));
        if (year != null) params.add(String.format("YEAR(F01_RELEASE_DATE) = %s", year));

        String sql =
                "SELECT f.*, COUNT(l.U01_ID) " +
                        "FROM F01_FILM as f " +
                        "LEFT JOIN L01_LIKES_FILM AS l ON l.F01_ID = f.F01_ID " +
                        "LEFT JOIN F02_FILM_GENRE AS fg on fg.F01_ID = f.F01_ID %s " +
                        "GROUP BY f.F01_ID " +
                        "ORDER BY COUNT(l.U01_ID) DESC " +
                        "LIMIT ?";

        String sqlParams = params.isEmpty() ? "" : "WHERE ".concat(String.join(" AND ", params));
        return jdbcTemplate.query(String.format(sql, sqlParams), (rs, rowNum) -> makeFilm(rs), count);
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
