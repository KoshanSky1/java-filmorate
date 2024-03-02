package ru.yandex.practicum.filmorate.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/api/films")
public class FilmController {

    public final static LocalDate ERROR_DATE = LocalDate.parse("1895-12-28");

    private final Map<Integer, Film> films = new HashMap<>();

    @SneakyThrows
    @PostMapping(value = "/film")
    public ResponseEntity<?> createFilm(@RequestBody @Valid Film film) {
        LocalDate newDate = new java.sql.Date(film.getReleaseDate().getTime()).toLocalDate();
        log.debug("newDate = " + newDate);
        if (newDate.isBefore(ERROR_DATE)) {
            throw new ValidationException("Date is before 1895-12-28");
        }
        films.put(film.getId(), film);
        log.info("Film was create");
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @PutMapping(value = "/film")
    public ResponseEntity<?> updateFilm(@RequestBody @Valid Film film) {
        films.put(film.getId(), film);
        log.info("Film was update");
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllFilms() {
        return new ResponseEntity<>(new ArrayList<>(films.values()), HttpStatus.OK);
    }
}
