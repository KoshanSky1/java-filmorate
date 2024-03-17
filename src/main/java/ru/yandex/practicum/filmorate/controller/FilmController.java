package ru.yandex.practicum.filmorate.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    FilmController(@Autowired(required = false) FilmService filmService) {
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
}
