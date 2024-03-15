package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

import static java.util.Calendar.DECEMBER;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private final LocalDate movieBirthday = LocalDate.of(1895, DECEMBER, 28);
    private Integer id = 0;

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if (films.containsValue(film)) {
            log.debug("Валидация не пройдена: такой фильм уже существует");
            throw new ValidationException("Такой фильм уже существует.");
        }
        if (film.getName() == null || film.getName().isBlank()) { // для тестов
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription().length() > 200) { // для тестов
            throw new ValidationException("Максимальная длина описания фильма — 200 символов.");
        }
        if (film.getDuration() < 0) { //для тестов
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
        if (film.getReleaseDate().isBefore(movieBirthday)) {
            log.debug("Валидация не пройдена: дата релиза ранее дня рождения кино");
            throw new ValidationException("Дата релиза фильма должна быть не раньше 28 декабря 1895 года.");
        }
        ++this.id;
        film.setId(id);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: " + film);
        return film;
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.debug("Обновление невозможно: фильм не найден");
            throw new ValidationException("Фильм не найден.");
        }
        if (film.getName() == null || film.getName().isBlank()) { // для тестов
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription().length() > 200) { // для тестов
            throw new ValidationException("Максимальная длина описания фильма — 200 символов.");
        }
        if (film.getDuration() < 0) { //для тестов
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
        if (film.getReleaseDate().isBefore(movieBirthday)) {
            log.debug("Валидация не пройдена: дата релиза ранее дня рождения кино");
            throw new ValidationException("Дата релиза фильма должна быть не раньше 28 декабря 1895 года.");
        }
        films.put(film.getId(), film);
        log.info("Фильм (id = " + film.getId() + ") успешно обновлён");
        return film;
    }

}