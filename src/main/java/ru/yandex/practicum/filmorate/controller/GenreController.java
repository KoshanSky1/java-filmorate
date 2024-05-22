package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final FilmService filmService;

    @Autowired
    public GenreController(@Qualifier("filmDbService") FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Genre> getAllGenres() {
        return filmService.getAllGenres();
    }

    @GetMapping("{genreId}")
    public Genre findRating(@PathVariable("genreId") Integer genreId) {
        return filmService.findGenreById(genreId);
    }

}