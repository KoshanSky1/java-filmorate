package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findFilmById(Integer filmId) {
        return filmStorage.findFilmById(filmId);
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film put(Film film) {
        return filmStorage.put(film);
    }

    public void delete(Film film) {
        filmStorage.delete(film);
    }

    public void addLike(Integer id, Integer userId) {
        filmStorage.addLike(id, userId);
    }

    public void deleteLike(Integer id, Integer userId) {
        filmStorage.deleteLike(id, userId);
    }

    public List<Film> displayPopularFilms(Integer count) {
        return filmStorage.displayPopularFilms(count);
    }

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre findGenreById(Integer id) {
        return filmStorage.findGenreById(id);
    }

    public List<Mpa> getAllRatings() {
        return filmStorage.getAllRatings();
    }

    public Mpa findRatingById(Integer id) {
        return filmStorage.findRatingById(id);
    }

    public Map<Integer, Film> getFilms() {
        return filmStorage.getFilms();
    }

}