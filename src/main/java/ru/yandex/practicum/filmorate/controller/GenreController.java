package ru.yandex.practicum.filmorate.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(@Autowired(required = false) GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public ResponseEntity<List<Genre>> findAllGenres() {
        log.info("---START FIND ALL GENRES ENDPOINT---");
        return new ResponseEntity<>(genreService.getAllGenres(), HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping("/{idGenre}")
    public ResponseEntity<Genre> findGenre(@PathVariable int idGenre) {
        log.info("---START FIND GENRE ENDPOINT---");
        log.info("GENRE = " + genreService.getGenre(idGenre));
        return new ResponseEntity<>(genreService.getGenre(idGenre), HttpStatus.OK);
    }
}
