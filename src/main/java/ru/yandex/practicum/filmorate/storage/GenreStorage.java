package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    List<Genre> getAllGenres();

    Genre getGenre(int idGenre);

    Genre createGenre(Genre genre);

    Genre updateGenre(Genre genre);

    boolean deleteGenre(int idGenre);

    List<Genre> getGenresListForFilm(int idFilm);
}
