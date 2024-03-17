package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    Comparator<Film> filmComparator = new Comparator<Film>() {
        @Override
        public int compare(Film f1, Film f2) {
            if (f1.getLikes() < f2.getLikes()) return 1;
            else if (f1.getLikes() == f2.getLikes()) return 0;
            else return -1;
        }
    };

    private final InMemoryFilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findFilmById(Integer filmId) {
        Film film = filmStorage.getFilms().get(filmId);
        if (film == null) throw new FilmNotFoundException(String.format("Фильм № %d не найден", filmId));
        return film;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film put(Film film) {
        return filmStorage.put(film);
    }

    public void delete(Film film) {
        filmStorage.delete(film);
    }

    public void addLike(Integer id, Integer userId) {
        User user = userService.getUsers().get(userId);
        Film film = getFilms().get(id);
        if (user == null) {
            log.debug(String.format("Пользователь № %d не найден", userId));
            throw new UserNotFoundException(String.format("Пользователь № %d не найден", userId));
        }
        if (film == null) {
            log.debug(String.format("Фильм № %d не найден", id));
            throw new FilmNotFoundException(String.format("Фильм № %d не найден", id));
        }
        int likes;
        int sizeOfListOfFilm = user.getFilms().size();
        user.getFilms().add(film.getId());
        if (user.getFilms().size() > sizeOfListOfFilm) {
            if (film.getLikes() == null) {
                likes = 0;
            } else {
                likes = film.getLikes();
            }
            film.setLikes(likes + 1);
        }
        log.info("Пользователь " + userId + " добавил лайк к фильму " + id);
    }

    public void deleteLike(Integer id, Integer userId) {
        User user = userService.getUsers().get(userId);
        Film film = getFilms().get(id);
        if (user == null) {
            log.debug(String.format("Пользователь № %d не найден", userId));
            throw new UserNotFoundException(String.format("Пользователь № %d не найден", userId));
        }
        if (film == null) {
            log.debug(String.format("Фильм № %d не найден", id));
            throw new FilmNotFoundException(String.format("Фильм № %d не найден", id));
        }
        if (user.getFilms().contains(id)) {
            user.getFilms().remove(id);
            film.setLikes(film.getLikes() - 1);
            log.info("Пользователь " + userId + " удалил лайк к фильму " + id);
        } else {
            log.info("Лайк к фильму " + id + " от пользователя " + userId + " не найден");
            throw new IncorrectParameterException("Лайк к фильму " + id + " от пользователя " + userId + " не найден");
        }
    }

    public List<Film> displayPopularFilms(Integer count) {
        List<Film> theTenMostPopularFilms = new ArrayList<>(filmStorage.findAll());
        if (theTenMostPopularFilms.isEmpty()) {
            log.debug("Список популярных фильмов пуст");
            throw new FilmNotFoundException("Популярный фильмы не найдены");
        }
        if (count >= theTenMostPopularFilms.size()) {
            count = theTenMostPopularFilms.size();
            log.debug("Размер списка популярных фильмов изменен и составит " + theTenMostPopularFilms.size());
        }
        log.debug("Сформирован список наиболее популярных фильмов");
        return theTenMostPopularFilms.stream()
                .filter(f -> f.getLikes() != null)
                .sorted(filmComparator)
                .limit(count)
                .collect(Collectors.toList());
    }

    public Map<Integer, Film> getFilms() {
        return filmStorage.getFilms();
    }

}