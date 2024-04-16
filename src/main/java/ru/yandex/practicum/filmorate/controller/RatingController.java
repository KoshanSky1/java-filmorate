package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class RatingController {
    private final FilmService filmService;

    @Autowired
    public RatingController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Mpa> getAllRatings() {
        return filmService.getAllRatings();
    }

    @GetMapping("{ratingId}")
    public Mpa findRating(@PathVariable("ratingId") Integer ratingId) {
        return filmService.findRatingById(ratingId);
    }

}