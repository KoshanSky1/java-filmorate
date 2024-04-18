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
public class InMemoryFilmService implements FilmService{
    private final FilmStorage filmStorage;

    @Autowired
    public InMemoryFilmService(@Qualifier("inMemoryFilmStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @Override
    public Film findFilmById(Integer filmId) {
        return filmStorage.findFilmById(filmId);
    }

    @Override
    public Film create(Film film) {
        return filmStorage.create(film);
    }

    @Override
    public Film put(Film film) {
        return filmStorage.put(film);
    }

    @Override
    public void delete(Film film) {
        filmStorage.delete(film);
    }

    @Override
    public void addLike(Integer id, Integer userId) {
        filmStorage.addLike(id, userId);
    }

    @Override
    public void deleteLike(Integer id, Integer userId) {
        filmStorage.deleteLike(id, userId);
    }

    @Override
    public List<Film> displayPopularFilms(Integer count) {
        return filmStorage.displayPopularFilms(count);
    }

    @Override
    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    @Override
    public Genre findGenreById(Integer id) {
        return filmStorage.findGenreById(id);
    }

    @Override
    public List<Mpa> getAllRatings() {
        return filmStorage.getAllRatings();
    }

    @Override
    public Mpa findRatingById(Integer id) {
        return filmStorage.findRatingById(id);
    }


}