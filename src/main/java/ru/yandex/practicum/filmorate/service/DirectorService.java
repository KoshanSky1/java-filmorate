package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {
    List<Director> getAllDirectors();

    Director getDirectorById(int idDirector);

    Director addDirectorToDB(Director director);

    Director updateDirector(Director director);

    boolean removeDirectorFromDB(Integer idDirector);

}
