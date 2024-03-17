package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private int id = 0;
    private final Map<Integer, Film> films = new HashMap<>();


    @Override
    public Film createFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            throw new ValidationException("Date is before 1895-12-28", HttpStatus.BAD_REQUEST.value());
        }
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("Film was create");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Film not exist");
        }
        films.put(film.getId(), film);
        log.info("Film was update");
        return film;
    }

    @Override
    public boolean deleteFilm(int idFilm) {
        return films.remove(idFilm, films.get(idFilm));
    }

    @Override
    public Film getFilm(int idFilm) {
        return films.get(idFilm);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

}
