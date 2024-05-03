package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreStorage genreStorage;

    @Autowired
    public GenreServiceImpl(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    @Override
    public Genre getGenre(int idGenre) {
        log.info(format("Start get idGenre = [%s]", idGenre));
        return genreStorage.getGenre(idGenre);
    }

    @Override
    public List<Genre> getAllGenres() {
        log.info("Start get all genres");
        return genreStorage.getAllGenres();
    }

}
