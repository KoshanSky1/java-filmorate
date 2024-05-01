package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
public class ReviewsService {
    private final ReviewsStorage reviewsStorage;

    @Autowired
    public ReviewsService(@Qualifier("ReviewsDbStorage") ReviewsStorage reviewsStorage) {
        this.reviewsStorage = reviewsStorage;
    }

    public Review getReview(int id) {
        log.info(format("Start get idFilm = [%s]", id));
        return reviewsStorage.getReview(id);
    }

    public List<Review> getAllReviews(int filmId, int count) {
        if (filmId == -1) {
            log.info(format("Start get all reviews limit = [%s]", count));
            return reviewsStorage.getAllReviews(count);
        } else {
            log.info(format("Start get all reviews limit = [%s] with idFilm = [%s]", count, filmId));
            return reviewsStorage.getAllReviewsByFilmId(filmId, count);
        }
    }

    public Review createReview(Review review) {
        log.info(format("Start create reviewId = [%s]", review.getReviewId()));
        return reviewsStorage.createReview(review);
    }

    public Review updateReview(Review review) {
        log.info(format("Start update reviewId = [%s]", review.getReviewId()));
        return reviewsStorage.updateReview(review);
    }

    public boolean deleteReview(int id) {
        log.info(format("Start delete reviewId = [%s]", id));
        return reviewsStorage.deleteReview(id);
    }

    public void addLike(int reviewId, int userId, boolean isPositive) {
        log.info(format("Start add like id = [%s] from user = [%s], positive = [%s]", reviewId, userId, isPositive));
        reviewsStorage.addLike(reviewId, userId, isPositive);
    }

    public void removeLike(int reviewId, int userId, boolean isPositive) {
        log.info(format("Start remove like reviewId = [%s] from userId = [%s]", reviewId, userId));
        reviewsStorage.removeLike(reviewId, userId, isPositive);
    }

}
