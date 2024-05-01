package ru.yandex.practicum.filmorate.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ErrorResponse;
import ru.yandex.practicum.filmorate.json.SuccessJSON;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewsService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/reviews")
public class ReviewsController {
    private final ReviewsService reviewsService;

    @Autowired
    public ReviewsController(ReviewsService reviewsService) {
        this.reviewsService = reviewsService;
    }

    @SneakyThrows
    @PostMapping
    public ResponseEntity<Review> createReview(@Validated @RequestBody Review review) {
        log.info("---START CREATE REVIEW ENDPOINT---");
        return new ResponseEntity<>(reviewsService.createReview(review), HttpStatus.OK);
    }

    @SneakyThrows
    @PutMapping
    public ResponseEntity<Review> updateReview(@Validated @RequestBody Review review) {
        log.info("---START UPDATE REVIEW ENDPOINT---");
        return new ResponseEntity<>(reviewsService.updateReview(review), HttpStatus.OK);
    }

    @SneakyThrows
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable int id) {
        log.info("---START DELETE REVIEW ENDPOINT---");
        if (reviewsService.deleteReview(id)) {
            return new ResponseEntity<>(new SuccessJSON("Review is deleted"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse("Error"), HttpStatus.BAD_REQUEST);
    }

    @SneakyThrows
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable int id) {
        log.info("---START GET REVIEW BY ID ENDPOINT---");
        return new ResponseEntity<>(reviewsService.getReview(id), HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews(@RequestParam(value = "filmId", defaultValue = "-1") int id,
                                                      @RequestParam(value = "count", defaultValue = "10") int count) {
        log.info("---START GET ALL REVIEWS ENDPOINT---");
        return new ResponseEntity<>(reviewsService.getAllReviews(id, count), HttpStatus.OK);
    }

    @SneakyThrows
    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<?> addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("---START ADD LIKE ENDPOINT---");
        reviewsService.addLike(id, userId, true);
        return new ResponseEntity<>(new SuccessJSON("Added like"), HttpStatus.OK);
    }

    @SneakyThrows
    @PutMapping("/{id}/dislike/{userId}")
    public ResponseEntity<?> addDislike(@PathVariable int id, @PathVariable int userId) {
        log.info("---START ADD DISLIKE ENDPOINT---");
        reviewsService.addLike(id, userId, false);
        return new ResponseEntity<>(new SuccessJSON("Added dislike"), HttpStatus.OK);
    }

    @SneakyThrows
    @DeleteMapping("{id}/like/{userId}")
    public ResponseEntity<?> removeLike(@PathVariable int id, @PathVariable int userId) {
        log.info("---START REMOVE LIKE ENDPOINT---");
        reviewsService.removeLike(id, userId, true);
        return new ResponseEntity<>(new SuccessJSON("Removed like"), HttpStatus.OK);
    }

    @SneakyThrows
    @DeleteMapping("{id}/dislike/{userId}")
    public ResponseEntity<?> removeDislike(@PathVariable int id, @PathVariable int userId) {
        log.info("---START REMOVE DISLIKE ENDPOINT---");
        reviewsService.removeLike(id, userId, false);
        return new ResponseEntity<>(new SuccessJSON("Removed dislike"), HttpStatus.OK);
    }
}
