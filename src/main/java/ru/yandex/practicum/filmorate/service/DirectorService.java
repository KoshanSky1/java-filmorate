package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
public class DirectorService {

    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> getAllDirectors() {
        log.info("Start get all director");
        return directorStorage.getAllDirectors();
    }

    public Director getDirectorById(int idDirector) {
        log.info("Start get director by id");
        return directorStorage.getDirectorById(idDirector);
    }

    public Director addDirectorToDB(Director director) {
        log.info("Add director: " + director + " to DB");
        return directorStorage.addDirectorToDatabase(director);
    }

    public Director updateDirector(Director director) {
        log.info(format("Start update idDirector = [%s]", director.getId()));
        return directorStorage.updateDirector(director);
    }

    public boolean removeDirectorFromDB(Integer idDirector) {
        log.info("Remove director: " + idDirector + " from DB");
        return directorStorage.removeDirectorFromDatabase(idDirector);
    }

}