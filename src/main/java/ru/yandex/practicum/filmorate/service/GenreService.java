package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
public class GenreService {

    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getGenre(int idGenre) {
        log.info(format("Start get idGenre = [%s]", idGenre));
        return genreStorage.getGenre(idGenre);
    }

    public List<Genre> getAllGenres() {
        log.info("Start get all genres");
        return genreStorage.getAllGenres();
    }

}
