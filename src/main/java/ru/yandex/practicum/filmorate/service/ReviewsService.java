package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewsService {
    Review getReview(int id);

    List<Review> getAllReviews(int filmId, int count);

    Review createReview(Review review);

    Review updateReview(Review review);

    boolean deleteReview(int id);

    void addLike(int reviewId, int userId, boolean isPositive);

    void removeLike(int reviewId, int userId, boolean isPositive);
}
