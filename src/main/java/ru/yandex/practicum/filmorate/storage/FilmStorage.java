package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(Film film);

    Film put(Film film);

    void delete(Film film);

    Map<Integer, Film> getFilms();

    Film findFilmById(Integer filmId);

    void addLike(Integer id, Integer userId);

    void deleteLike(Integer id, Integer userId);

    List<Film> displayPopularFilms(Integer count);

    List<Genre> getAllGenres();

    Genre findGenreById(Integer id);

    List<Mpa> getAllRatings();

    Mpa findRatingById(Integer id);
}