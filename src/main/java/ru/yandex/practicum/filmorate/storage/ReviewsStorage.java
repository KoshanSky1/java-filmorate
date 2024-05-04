package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewsStorage {
    List<Review> getAllReviewsByFilmId(int filmId, int count);

    List<Review> getAllReviews(int count);

    Review getReview(int reviewId);

    Review createReview(Review review);

    Review updateReview(Review review);

    boolean deleteReview(int reviewId);

    void addLike(int reviewId, int userId, boolean isPositive);

    void removeLike(int reviewId, int userId, boolean isPositive);

    Integer getLikeCount(int reviewId);
}
