package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.ReviewCreateException;
import ru.yandex.practicum.filmorate.exeption.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.feed.Operation;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

@Repository("ReviewsDbStorage")
@RequiredArgsConstructor
public class ReviewsDbStorage implements ReviewsStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedDbStorage feedDbStorage;

    @Override
    public List<Review> getAllReviewsByFilmId(int filmId, int count) {
        String sql = "select * from R01_REVIEWS WHERE F01_ID = ? ORDER BY R01_USEFUL DESC  LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), filmId, count);
    }

    @Override
    public List<Review> getAllReviews(int count) {
        String sql = "select * from R01_REVIEWS ORDER BY R01_USEFUL DESC LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), 10);
    }

    @Override
    public Review getReview(int reviewId) {
        String sql =
                "select * " +
                        "from R01_REVIEWS " +
                        "where R01_ID = ?";
        Review review;
        try {
            review = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeReview(rs), reviewId);
        } catch (EmptyResultDataAccessException e) {
            throw new ReviewNotFoundException(format("Review [%s] not found in DB", reviewId));
        }
        return review;
    }

    @Override
    public Review createReview(Review review) {
        String sql =
                "insert into R01_REVIEWS " +
                        "(U01_ID, F01_ID, R01_CONTENT, R01_IS_POSITIVE, R01_USEFUL) " +
                        "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        User user = userStorage.getUser(review.getUserId());
        Film film = filmStorage.getFilm(review.getFilmId());
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, user.getId());
                stmt.setInt(2, film.getId());
                stmt.setString(3, review.getContent());
                stmt.setBoolean(4, review.getIsPositive());
                stmt.setInt(5, review.getUseful());
                return stmt;
            }, keyHolder);
        } catch (Exception e) {
            throw new ReviewCreateException(e.getMessage());
        }
        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        feedDbStorage.addEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(user.getId())
                .entityId(id)
                .eventType(EventType.REVIEW)
                .operation(Operation.ADD)
                .build());
        return getReview(id);
    }

    @Override
    public Review updateReview(Review changedReview) {
        Review review = getReview(changedReview.getReviewId());
        review.setIsPositive(changedReview.getIsPositive());
        review.setContent(changedReview.getContent());
        changeReview(review);
        feedDbStorage.addEvent(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(review.getUserId())
                .entityId(review.getReviewId())
                .eventType(EventType.REVIEW)
                .operation(Operation.UPDATE)
                .build());
        return review;
    }

    @Override
    public boolean deleteReview(int reviewId) {
        Review review = getReview(reviewId);
        String sqlQuery =
                "delete " +
                        "from R01_REVIEWS " +
                        "where R01_ID = ?";

        boolean isDeleted = jdbcTemplate.update(sqlQuery, reviewId) > 0;
        if (isDeleted) {
            feedDbStorage.addEvent(Event.builder()
                    .timestamp(Instant.now().toEpochMilli())
                    .userId(review.getUserId())
                    .entityId(review.getReviewId())
                    .eventType(EventType.REVIEW)
                    .operation(Operation.REMOVE)
                    .build());
        }
        return isDeleted;
    }

    @Override
    public void addLike(int reviewId, int userId, boolean isPositive) {
        String sql = "insert into R02_REVIEWS_LIKES " +
                "(R01_ID, R02_IS_POSITIVE, U01_ID) " +
                "values (?, ?, ?) ";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, reviewId);
                preparedStatement.setBoolean(2, isPositive);
                preparedStatement.setInt(3, userId);
                return preparedStatement;
            }, keyHolder);
            changeReview(getReview(reviewId));
        } catch (Exception e) {
            throw new ValidationException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public void removeLike(int reviewId, int userId, boolean isPositive) {
        String sql =
                "delete from R02_REVIEWS_LIKES " +
                        "where R01_ID = ? and U01_ID = ? and R02_IS_POSITIVE = ? ";

        jdbcTemplate.update(sql, reviewId, userId, isPositive);
        changeReview(getReview(reviewId));
    }

    @Override
    public Integer getLikeCount(int reviewId) {
        String sql = "SELECT COUNT(CASE WHEN R02_IS_POSITIVE = TRUE THEN 1 END) - " +
                "COUNT(CASE WHEN R02_IS_POSITIVE = FALSE THEN 1 END)\n" +
                "FROM R02_REVIEWS_LIKES \n" +
                "WHERE R01_ID = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, reviewId);
    }

    private Review changeReview(Review review) {
        String sql =
                "update R01_REVIEWS set " +
                        "R01_CONTENT = ?, R01_IS_POSITIVE = ?, " +
                        "R01_USEFUL = ?" +
                        "where R01_ID = ?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                getLikeCount(review.getReviewId()),
                review.getReviewId());

        return getReview(review.getReviewId());
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        int reviewId = rs.getInt("R01_ID");
        return new Review(reviewId,
                rs.getString("R01_CONTENT"),
                rs.getBoolean("R01_IS_POSITIVE"),
                rs.getInt("U01_ID"),
                rs.getInt("F01_ID"),
                getLikeCount(reviewId));
    }
}
