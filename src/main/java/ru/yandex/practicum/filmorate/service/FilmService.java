package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }


    public Film createFilm(Film film) {
        log.info(format("Start create idFilm = [%s]", film.getId()));
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            throw new ValidationException("Date is before 1895-12-28", HttpStatus.BAD_REQUEST.value());
        }
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        log.info(format("Start update idFilm = [%s]", film.getId()));
        return filmStorage.updateFilm(film);
    }

    public boolean deleteFilm(int idFilm) {
        log.info(format("Start delete idFilm = [%s]", idFilm));
        return filmStorage.deleteFilm(idFilm);
    }

    public Film getFilm(int idFilm) {
        log.info(format("Start get idFilm = [%s]", idFilm));
        return filmStorage.getFilm(idFilm);
    }

    public List<Film> getAllFilms() {
        log.info("Start get all films");
        return filmStorage.getAllFilms();
    }

    public void addLike(int idFilm, int idUser) {
        log.info(format("Start add like idUser = [%s] to idFilm = [%s]", idUser, idFilm));
        Film film = getFilm(idFilm);
        film.addLike(idUser);
        updateFilm(film);
        log.info(format("Like added to idFilm = [%s]", idFilm));
    }

    public void deleteLike(int idFilm, int idUser) {
        log.info(format("Start delete like idUser = [%s] from idFilm = [%s]", idUser, idFilm));
        Film film = getFilm(idFilm);
        film.deleteLike(idUser);
        updateFilm(film);
        log.info(format("Like was delete to idFilm = [%s]", idFilm));
    }

    /**
     * Возвращает список из первых count фильмов по количеству лайков.
     * Если значение параметра count не задано, верните первые 10.
     *
     * @param count количества
     * @return список фильмов
     */
    public List<Film> getPopularFilms(int count) {
        log.info(format("Start get popular films count = [%s]", count));
        return getAllFilms().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

}
