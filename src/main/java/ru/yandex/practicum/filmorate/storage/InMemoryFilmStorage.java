package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Calendar.DECEMBER;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final UserStorage userStorage = new InMemoryUserStorage();
    @Getter
    private final Map<Integer, Film> films = new HashMap<>();
    private final LocalDate movieBirthday = LocalDate.of(1895, DECEMBER, 28);
    private Integer id = 0;

    Comparator<Film> filmComparator = new Comparator<Film>() {
        @Override
        public int compare(Film f1, Film f2) {
            if (f1.getLikes() < f2.getLikes()) return 1;
            else if (f1.getLikes() == f2.getLikes()) return 0;
            else return -1;
        }
    };

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        if (films.containsValue(film)) {
            log.debug("Валидация не пройдена: такой фильм уже существует");
            throw new ValidationException("Такой фильм уже существует.");
        }
        if (film.getName() == null || film.getName().isBlank()) { // для тестов
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription().length() > 200) { // для тестов
            throw new ValidationException("Максимальная длина описания фильма — 200 символов.");
        }
        if (film.getDuration() < 0) { //для тестов
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
        if (film.getReleaseDate().isBefore(movieBirthday)) {
            log.debug("Валидация не пройдена: дата релиза ранее дня рождения кино");
            throw new ValidationException("Дата релиза фильма должна быть не раньше 28 декабря 1895 года.");
        }
        ++this.id;
        film.setId(id);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: " + film);
        return film;
    }

    @Override
    public Film put(Film film) {
        if (!films.containsKey(film.getId())) {
            log.debug("Обновление невозможно: фильм не найден");
            throw new ValidationException("Фильм не найден.");
        }
        if (film.getName() == null || film.getName().isBlank()) { // для тестов
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription().length() > 200) { // для тестов
            throw new ValidationException("Максимальная длина описания фильма — 200 символов.");
        }
        if (film.getDuration() < 0) { //для тестов
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
        if (film.getReleaseDate().isBefore(movieBirthday)) {
            log.debug("Валидация не пройдена: дата релиза ранее дня рождения кино");
            throw new ValidationException("Дата релиза фильма должна быть не раньше 28 декабря 1895 года.");
        }
        films.put(film.getId(), film);
        log.info("Фильм (id = " + film.getId() + ") успешно обновлён");
        return film;
    }

    @Override
    public void delete(Film film) {
        if (films.containsValue(film)) {
            films.remove(film.getId());
            log.debug("Фильм " + film.getId() + " успешно удалён");
        } else {
            log.debug("Фильм не найден");
            throw new ValidationException("Фильм " + film.getId() + " не найден");
        }
    }

    @Override
    public Film findFilmById(Integer filmId) {
        Film film = getFilms().get(filmId);
        if (film == null) throw new FilmNotFoundException(String.format("Фильм № %d не найден", filmId));
        return film;
    }

    @Override
    public void addLike(Integer id, Integer userId) {
        User user = userStorage.getUsers().get(userId);
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

    @Override
    public void deleteLike(Integer id, Integer userId) {
        User user = userStorage.getUsers().get(userId);
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

    @Override
    public List<Film> displayPopularFilms(Integer count) {
        List<Film> theTenMostPopularFilms = new ArrayList<>(findAll());
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

    @Override
    public List<Genre> getAllGenres() {
        return null;
    }

    @Override
    public Genre findGenreById(Integer id) {
        return null;
    }

    @Override
    public List<Mpa> getAllRatings() {
        return null;
    }

    @Override
    public Mpa findRatingById(Integer id) {
        return null;
    }

}