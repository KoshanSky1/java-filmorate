package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void findAll() {
        Mpa mpaNumberOne = Mpa.builder()
                .id(3)
                .name("PG-13")
                .build();

        Mpa mpaNumberTwo = Mpa.builder()
                .id(5)
                .name("NC-17")
                .build();

        Set<Genre> genresOfFilmNumberOne = new HashSet<>();
        Set<Genre> genresOfFilmNumberTwo = new HashSet<>();

        Genre genreNumberOne = Genre.builder()
                .id(5)
                .name("Документальный")
                .build();

        Genre genreNumberTwo = Genre.builder()
                .id(4)
                .name("Триллер")
                .build();

        genresOfFilmNumberOne.add(genreNumberOne);
        genresOfFilmNumberTwo.add(genreNumberTwo);

        Film filmNumberOne = Film.builder()
                .name("Фильм номер 1")
                .description("Описание фильма номер 1")
                .releaseDate(LocalDate.of(1997, Month.DECEMBER, 14))
                .duration(154L)
                .mpa(mpaNumberOne)
                .genres(genresOfFilmNumberOne)
                .build();

        Film filmNumberTwo = Film.builder()
                .name("Фильм номер 2")
                .description("Описание фильма номер 2")
                .releaseDate(LocalDate.of(1997, Month.DECEMBER, 14))
                .duration(154L)
                .mpa(mpaNumberTwo)
                .genres(genresOfFilmNumberTwo)
                .build();

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.create(filmNumberOne);
        filmDbStorage.create(filmNumberTwo);

        Map<Integer, Film> films = new HashMap<>();
        films.put(1, filmNumberOne);
        films.put(2, filmNumberTwo);

        Collection<Film> savedFilms = filmDbStorage.findAll();

        assertThat(savedFilms)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(films.values());
    }

    @Test
    public void findFilmById() {
        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();

        Set<Genre> genres = new HashSet<>();

        Genre genre = Genre.builder()
                .id(1)
                .name("Комедия")
                .build();

        genres.add(genre);
        Film filmNumberOne = Film.builder()
                .name("Фильм номер 1")
                .description("Описани фильма номер 1")
                .releaseDate(LocalDate.of(1997, Month.DECEMBER, 14))
                .duration(154L)
                .mpa(mpa)
                .genres(genres)
                .build();

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.create(filmNumberOne);

        Film savedFilm = filmDbStorage.findFilmById(filmNumberOne.getId());

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmNumberOne);
    }

    @Test
    public void create() {
        Mpa mpa = Mpa.builder()
                .id(4)
                .name("R")
                .build();

        Set<Genre> genres = new HashSet<>();

        Genre genre = Genre.builder()
                .id(2)
                .name("Драма")
                .build();

        genres.add(genre);
        Film filmNumberOne = Film.builder()
                .name("Титаник")
                .description("Американская эпическая романтическая драма")
                .releaseDate(LocalDate.of(2018, Month.FEBRUARY, 8))
                .duration(133L)
                .mpa(mpa)
                .genres(genres)
                .build();

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.create(filmNumberOne);

        Film savedFilm = filmDbStorage.findFilmById(1);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmNumberOne);
    }

    @Test
    public void put() {
        Mpa mpa = Mpa.builder()
                .id(4)
                .name("R")
                .build();

        Set<Genre> genres = new HashSet<>();

        Genre genre = Genre.builder()
                .id(2)
                .name("Драма")
                .build();

        genres.add(genre);
        Film filmNumberOne = Film.builder()
                .name("Титаник")
                .description("Американская эпическая романтическая драма")
                .releaseDate(LocalDate.of(2018, Month.FEBRUARY, 8))
                .duration(133L)
                .mpa(mpa)
                .genres(genres)
                .build();

        Film filmNumberOneUpdated = Film.builder()
                .id(1)
                .name("Обновленный фильм номер 1")
                .description("Обновленное описание фильма номер 1")
                .releaseDate(LocalDate.of(2007, Month.DECEMBER, 26))
                .duration(154L)
                .mpa(mpa)
                .genres(genres)
                .build();

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.create(filmNumberOne);
        filmNumberOneUpdated.setId(filmNumberOne.getId());
        filmDbStorage.put(filmNumberOneUpdated);

        Film savedFilm = filmDbStorage.findFilmById(filmNumberOne.getId());

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmNumberOneUpdated);
    }

    @Test
    public void delete() {
        Mpa mpaNumberOne = Mpa.builder()
                .id(3)
                .name("PG-13")
                .build();

        Mpa mpaNumberTwo = Mpa.builder()
                .id(5)
                .name("NC-17")
                .build();

        Set<Genre> genresOfFilmNumberOne = new HashSet<>();
        Set<Genre> genresOfFilmNumberTwo = new HashSet<>();

        Genre genreNumberOne = Genre.builder()
                .id(5)
                .name("Документальный")
                .build();

        Genre genreNumberTwo = Genre.builder()
                .id(4)
                .name("Триллер")
                .build();

        genresOfFilmNumberOne.add(genreNumberOne);
        genresOfFilmNumberTwo.add(genreNumberTwo);

        Film filmNumberOne = Film.builder()
                .name("Фильм номер 1")
                .description("Описание фильма номер 1")
                .releaseDate(LocalDate.of(1997, Month.DECEMBER, 14))
                .duration(154L)
                .mpa(mpaNumberOne)
                .genres(genresOfFilmNumberOne)
                .build();

        Film filmNumberTwo = Film.builder()
                .name("Фильм номер 2")
                .description("Описание фильма номер 2")
                .releaseDate(LocalDate.of(1997, Month.DECEMBER, 14))
                .duration(154L)
                .mpa(mpaNumberTwo)
                .genres(genresOfFilmNumberTwo)
                .build();

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.create(filmNumberOne);
        filmDbStorage.create(filmNumberTwo);
        filmDbStorage.delete(filmNumberOne);

        Map<Integer, Film> films = new HashMap<>();
        films.put(1, filmNumberOne);
        films.put(2, filmNumberTwo);
        films.remove(1);

        Collection<Film> savedFilms = filmDbStorage.findAll();

        assertThat(savedFilms)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(films.values());
    }

    @Test
    public void addLike() {
        User userNumberOne = User.builder()
                .email("test@mail.ru")
                .login("test")
                .name("Тeст")
                .birthday(LocalDate.of(1993, Month.MAY, 8))
                .build();

        User userNumberTwo = User.builder()
                .email("test2@mail.ru")
                .login("test2")
                .name("Тeст2")
                .birthday(LocalDate.of(1993, Month.MAY, 8))
                .build();

        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        userNumberOne = userDbStorage.create(userNumberOne);
        userNumberTwo = userDbStorage.create(userNumberTwo);

        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();

        Set<Genre> genres = new HashSet<>();

        Genre genre = Genre.builder()
                .id(1)
                .name("Комедия")
                .build();

        genres.add(genre);

        Film filmNumberOne = Film.builder()
                .name("Фильм номер 1")
                .description("Описани фильма номер 1")
                .releaseDate(LocalDate.of(1997, Month.DECEMBER, 14))
                .duration(154L)
                .mpa(mpa)
                .genres(genres)
                .build();

        Film filmNumberTwo = Film.builder()
                .name("Фильм номер 2")
                .description("Описание фильма номер 2")
                .releaseDate(LocalDate.of(1997, Month.DECEMBER, 14))
                .duration(154L)
                .mpa(mpa)
                .genres(genres)
                .build();

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.create(filmNumberOne);
        filmDbStorage.create(filmNumberTwo);

        filmDbStorage.addLike(filmNumberTwo.getId(), 1);
        filmDbStorage.addLike(filmNumberTwo.getId(), 2);

        List<Film> popularFilms = new ArrayList<>();
        popularFilms.add(filmNumberTwo);
        popularFilms.add(filmNumberOne);

        List<Film> films = filmDbStorage.displayPopularFilms(2);

        assertThat(films)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(popularFilms);
    }

    @Test
    public void deleteLike() {
        User userNumberOne = User.builder()
                .email("test@mail.ru")
                .login("test")
                .name("Тeст")
                .birthday(LocalDate.of(1993, Month.MAY, 8))
                .build();

        User userNumberTwo = User.builder()
                .email("test2@mail.ru")
                .login("test2")
                .name("Тeст2")
                .birthday(LocalDate.of(1993, Month.MAY, 8))
                .build();

        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        userNumberOne = userDbStorage.create(userNumberOne);
        userNumberTwo = userDbStorage.create(userNumberTwo);

        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();

        Set<Genre> genres = new HashSet<>();

        Genre genre = Genre.builder()
                .id(1)
                .name("Комедия")
                .build();

        genres.add(genre);

        Film filmNumberOne = Film.builder()
                .name("Фильм номер 1")
                .description("Описани фильма номер 1")
                .releaseDate(LocalDate.of(1997, Month.DECEMBER, 14))
                .duration(154L)
                .mpa(mpa)
                .genres(genres)
                .build();

        Film filmNumberTwo = Film.builder()
                .name("Фильм номер 2")
                .description("Описание фильма номер 2")
                .releaseDate(LocalDate.of(1997, Month.DECEMBER, 14))
                .duration(154L)
                .mpa(mpa)
                .genres(genres)
                .build();

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmNumberOne = filmDbStorage.create(filmNumberOne);
        filmNumberTwo = filmDbStorage.create(filmNumberTwo);

        filmDbStorage.addLike(filmNumberOne.getId(), userNumberOne.getId());
        filmDbStorage.addLike(filmNumberOne.getId(), userNumberTwo.getId());
        filmDbStorage.addLike(filmNumberTwo.getId(), userNumberOne.getId());
        filmDbStorage.addLike(filmNumberTwo.getId(), userNumberTwo.getId());
        filmDbStorage.deleteLike(filmNumberOne.getId(), userNumberOne.getId());

        List<Film> popularFilms = new ArrayList<>();
        popularFilms.add(filmNumberTwo);
        popularFilms.add(filmNumberOne);

        List<Film> films = filmDbStorage.displayPopularFilms(2);

        assertThat(films)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(popularFilms);
    }

    @Test
    public void displayPopularFilms() {
        User userNumberOne = User.builder()
                .email("test@mail.ru")
                .login("test")
                .name("Тeст")
                .birthday(LocalDate.of(1993, Month.MAY, 8))
                .build();

        User userNumberTwo = User.builder()
                .email("test2@mail.ru")
                .login("test2")
                .name("Тeст2")
                .birthday(LocalDate.of(1993, Month.MAY, 8))
                .build();

        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        userNumberOne = userDbStorage.create(userNumberOne);
        userNumberTwo = userDbStorage.create(userNumberTwo);

        Mpa mpa = Mpa.builder()
                .id(1)
                .name("G")
                .build();

        Set<Genre> genres = new HashSet<>();

        Genre genre = Genre.builder()
                .id(1)
                .name("Комедия")
                .build();

        genres.add(genre);

        Film filmNumberOne = Film.builder()
                .name("Фильм номер 1")
                .description("Описани фильма номер 1")
                .releaseDate(LocalDate.of(1997, Month.DECEMBER, 14))
                .duration(154L)
                .mpa(mpa)
                .genres(genres)
                .build();

        Film filmNumberTwo = Film.builder()
                .name("Фильм номер 2")
                .description("Описание фильма номер 2")
                .releaseDate(LocalDate.of(1997, Month.DECEMBER, 14))
                .duration(154L)
                .mpa(mpa)
                .genres(genres)
                .build();

        Film filmNumberThree = Film.builder()
                .name("Фильм номер 3")
                .description("Описание фильма номер 3")
                .releaseDate(LocalDate.of(2018, Month.FEBRUARY, 8))
                .duration(154L)
                .mpa(mpa)
                .genres(genres)
                .build();

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmNumberOne = filmDbStorage.create(filmNumberOne);
        filmNumberTwo = filmDbStorage.create(filmNumberTwo);
        filmNumberThree = filmDbStorage.create(filmNumberThree);

        filmDbStorage.addLike(filmNumberOne.getId(), userNumberOne.getId());
        filmDbStorage.addLike(filmNumberOne.getId(), userNumberTwo.getId());
        filmDbStorage.addLike(filmNumberTwo.getId(), userNumberOne.getId());
        filmDbStorage.addLike(filmNumberTwo.getId(), userNumberTwo.getId());
        filmDbStorage.addLike(filmNumberThree.getId(), userNumberOne.getId());
        filmDbStorage.addLike(filmNumberThree.getId(), userNumberTwo.getId());
        filmDbStorage.deleteLike(filmNumberOne.getId(), userNumberOne.getId());
        filmDbStorage.deleteLike(filmNumberTwo.getId(), userNumberOne.getId());
        filmDbStorage.deleteLike(filmNumberTwo.getId(), userNumberTwo.getId());

        List<Film> popularFilms = new ArrayList<>();
        popularFilms.add(filmNumberThree);
        popularFilms.add(filmNumberOne);
        popularFilms.add(filmNumberTwo);

        List<Film> films = filmDbStorage.displayPopularFilms(3);

        assertThat(films)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(popularFilms);
    }

    @Test
    public void getAllGenres() {
        Genre genreNumberOne = Genre.builder()
                .id(1)
                .name("Комедия")
                .build();

        Genre genreNumberTwo = Genre.builder()
                .id(2)
                .name("Драма")
                .build();

        Genre genreNumberThree = Genre.builder()
                .id(3)
                .name("Мультфильм")
                .build();

        Genre genreNumberFour = Genre.builder()
                .id(4)
                .name("Триллер")
                .build();

        Genre genreNumberFive = Genre.builder()
                .id(5)
                .name("Документальный")
                .build();

        Genre genreNumberSix = Genre.builder()
                .id(6)
                .name("Боевик")
                .build();

        List<Genre> genresExpected = new ArrayList<>();
        genresExpected.add(genreNumberOne);
        genresExpected.add(genreNumberTwo);
        genresExpected.add(genreNumberThree);
        genresExpected.add(genreNumberFour);
        genresExpected.add(genreNumberFive);
        genresExpected.add(genreNumberSix);

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        List<Genre> genresActual = filmDbStorage.getAllGenres();

        assertThat(genresActual)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(genresExpected);
    }

    @Test
    public void findGenreById() {
        Genre genreNumberFour = Genre.builder()
                .id(4)
                .name("Триллер")
                .build();

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Genre genreActual = filmDbStorage.findGenreById(4);

        assertThat(genreActual)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(genreNumberFour);
    }

    @Test
    public void getAllRatings() {
        Mpa mpaNumberOne = Mpa.builder()
                .id(1)
                .name("G")
                .build();

        Mpa mpaNumberTwo = Mpa.builder()
                .id(2)
                .name("PG")
                .build();

        Mpa mpaNumberThree = Mpa.builder()
                .id(3)
                .name("PG-13")
                .build();

        Mpa mpaNumberFour = Mpa.builder()
                .id(4)
                .name("R")
                .build();

        Mpa mpaNumberFive = Mpa.builder()
                .id(5)
                .name("NC-17")
                .build();

        List<Mpa> ratingExpected = new ArrayList<>();
        ratingExpected.add(mpaNumberOne);
        ratingExpected.add(mpaNumberTwo);
        ratingExpected.add(mpaNumberThree);
        ratingExpected.add(mpaNumberFour);
        ratingExpected.add(mpaNumberFive);

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        List<Mpa> ratingActual = filmDbStorage.getAllRatings();

        assertThat(ratingActual)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(ratingExpected);
    }

    @Test
    public void findRatingById() {
        Mpa mpaNumberFour = Mpa.builder()
                .id(4)
                .name("R")
                .build();

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Mpa ratingActual = filmDbStorage.findRatingById(4);

        assertThat(ratingActual)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(mpaNumberFour);
    }

}