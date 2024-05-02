package ru.yandex.practicum.filmorate.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ErrorResponse;
import ru.yandex.practicum.filmorate.json.SuccessJSON;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @SneakyThrows
    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        log.info("---START CREATE FILM ENDPOINT---");
        return new ResponseEntity<>(filmService.createFilm(film), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        log.info("---START UPDATE FILM ENDPOINT---");
        return new ResponseEntity<>(filmService.updateFilm(film), HttpStatus.OK);
    }

    @SneakyThrows
    @DeleteMapping("/{idFilm}")
    public ResponseEntity<?> deleteFilm(@PathVariable int idFilm) {
        log.info("---START DELETE FILM ENDPOINT---");
        if (filmService.deleteFilm(idFilm)) {
            return new ResponseEntity<>(new SuccessJSON("Film was delete"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse("Error"), HttpStatus.BAD_REQUEST);
    }

    @SneakyThrows
    @GetMapping("/{idFilm}")
    public ResponseEntity<Film> findFilm(@PathVariable int idFilm) {
        log.info("---START FIND FILM ENDPOINT---");
        log.info("Film = " + filmService.getFilm(idFilm).toString());
        return new ResponseEntity<>(filmService.getFilm(idFilm), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Film>> findAllFilms() {
        log.info("---START FIND ALL FILMS ENDPOINT---");
        return new ResponseEntity<>(filmService.getAllFilms(), HttpStatus.OK);
    }

    @SneakyThrows
    @PutMapping("/{idFilm}/like/{idUser}")
    public ResponseEntity<SuccessJSON> addLike(@PathVariable int idFilm,
                                               @PathVariable int idUser) {
        log.info("---START ADD LIKE ENDPOINT---");
        filmService.addLike(idFilm, idUser);
        return new ResponseEntity<>(new SuccessJSON("Like added"), HttpStatus.OK);
    }

    @SneakyThrows
    @DeleteMapping("/{idFilm}/like/{idUser}")
    public ResponseEntity<SuccessJSON> deleteLike(@PathVariable int idFilm,
                                                  @PathVariable int idUser) {
        log.info("---START DELETE LIKE ENDPOINT---");
        filmService.deleteLike(idFilm, idUser);
        return new ResponseEntity<>(new SuccessJSON("Like was delete"), HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getMostPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("---START GET MOST POPULAR FILMS ENDPOINT---");
        return new ResponseEntity<>(filmService.getPopularFilms(count), HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping("/common")
    public ResponseEntity<List<Film>> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        log.info("---START GET COMMON FILMS ENDPOINT---");
        return new ResponseEntity<>(filmService.getCommonFilms(userId, friendId), HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping("/director/{idDirector}")
    public ResponseEntity<List<Film>> searchFilmsByDirector(@PathVariable("idDirector") int idDirector,
                                                            @RequestParam(required = false) String sortBy) {
        if (sortBy.equals("year")) {
            log.info("---START SEARCH FILMS BY DIRECTOR, SORTED BY YEAR ENDPOINT---");
            return new ResponseEntity<>(filmService.searchFilmsByDirectorSortedByYear(idDirector), HttpStatus.OK);
        } else if (sortBy.equals("likes")) {
            log.info("---START SEARCH FILMS BY DIRECTOR, SORTED BY LIKES ENDPOINT---");
            return new ResponseEntity<>(filmService.searchFilmsByDirectorSortedByLikes(idDirector), HttpStatus.OK);
        } else {
            log.info("---START SEARCH FILMS BY DIRECTOR, NO SORTED ENDPOINT---");
            return new ResponseEntity<>(filmService.searchFilmsByDirector(idDirector), HttpStatus.OK);
        }
    }

}