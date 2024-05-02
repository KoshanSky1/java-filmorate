package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorStorage {
    List<Director> getAllDirectors();

    Director getDirectorById(int idDirector);

    Director addDirectorToDatabase(Director director);

    Director updateDirector(Director director);

    boolean removeDirectorFromDatabase(int idDirector);

    List<Film> searchFilmsByDirector(Integer idDirector);

    List<Film> searchFilmsByDirectorSortedByYear(Integer idDirector);

    List<Film> searchFilmsByDirectorSortedByLikes(Integer idDirector);

}