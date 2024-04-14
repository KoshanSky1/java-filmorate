package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenreStorage {

    void deleteFilmGenre(long idFilm);

    List<Genre> createFilmGenre(int idFilm, List<Genre> genres);
}
