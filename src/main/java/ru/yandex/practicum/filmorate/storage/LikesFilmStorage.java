package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface LikesFilmStorage {
    void addLike(int idFilm, int idUser);

    void deleteLike(int idFilm, int idUser);

    List<Film> getPopularFilms(Integer count);
}
