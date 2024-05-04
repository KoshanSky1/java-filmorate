package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewsService;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
public class ReviewsServiceImpl implements ReviewsService {
    private final ReviewsStorage reviewsStorage;

    @Autowired
    public ReviewsServiceImpl(@Qualifier("ReviewsDbStorage") ReviewsStorage reviewsStorage) {
        this.reviewsStorage = reviewsStorage;
    }

    @Override
    public Review getReview(int id) {
        log.info(format("Start get idFilm = [%s]", id));
        return reviewsStorage.getReview(id);
    }

    @Override
    public List<Review> getAllReviews(int filmId, int count) {
        if (filmId == -1) {
            log.info(format("Start get all reviews limit = [%s]", count));
            return reviewsStorage.getAllReviews(count);
        } else {
            log.info(format("Start get all reviews limit = [%s] with idFilm = [%s]", count, filmId));
            return reviewsStorage.getAllReviewsByFilmId(filmId, count);
        }
    }

    @Override
    public Review createReview(Review review) {
        log.info(format("Start create reviewId = [%s]", review.getReviewId()));
        return reviewsStorage.createReview(review);
    }

    @Override
    public Review updateReview(Review review) {
        log.info(format("Start update reviewId = [%s]", review.getReviewId()));
        return reviewsStorage.updateReview(review);
    }

    @Override
    public boolean deleteReview(int id) {
        log.info(format("Start delete reviewId = [%s]", id));
        return reviewsStorage.deleteReview(id);
    }

    @Override
    public void addLike(int reviewId, int userId, boolean isPositive) {
        log.info(format("Start add like id = [%s] from user = [%s], positive = [%s]", reviewId, userId, isPositive));
        reviewsStorage.addLike(reviewId, userId, isPositive);
    }

    @Override
    public void removeLike(int reviewId, int userId, boolean isPositive) {
        log.info(format("Start remove like reviewId = [%s] from userId = [%s]", reviewId, userId));
        reviewsStorage.removeLike(reviewId, userId, isPositive);
    }

}
