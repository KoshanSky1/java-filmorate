package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    boolean deleteFilm(int idFilm);

    Film getFilm(int idFilm);

    List<Film> getAllFilms();

    void addLike(int idFilm, int idUser);

    void deleteLike(int idFilm, int idUser);

    List<Film> getPopularFilms(int count, Integer genreId, Integer year);

    List<Film> getCommonFilms(int idUser, int idFriend);

    List<Film> searchFilmsByDirector(int idDirector);

    List<Film> searchFilmsByDirectorSortedByYear(int idDirector);

    List<Film> searchFilmsByDirectorSortedByLikes(int idDirector);

    List<Film> searchFilms(String query, String by);
}
