package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmDbStorageTest {

    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;
    private final Genre genre = new Genre(1, "Комедия");
    private final Mpa mpa = new Mpa(1, "G");
    private final Director director = new Director(1, "Режиссер");
    private final Film film1 = new Film(1, "God Father", "Film about father",
            LocalDate.now(), 240, mpa, List.of(genre), List.of(director));
    private final Film film2 = new Film(2, "God Father2", "Film about father2",
            LocalDate.now(), 240, mpa, List.of(genre), List.of(director));
    private final Film film3 = new Film(3, "God Father3", "Film about father3",
            LocalDate.now(), 240, mpa, List.of(genre), List.of(director));
    private final Film film4 = new Film(4, "God Father4", "Film about father4",
            LocalDate.now(), 240, mpa, List.of(genre), List.of(director));


    @BeforeEach
    void beforeEach() {
        directorStorage.addDirectorToDatabase(director);

        filmStorage.createFilm(film1);
        filmStorage.createFilm(film2);
        filmStorage.createFilm(film3);
    }


    @Test
    @SneakyThrows
    public void testAddFilm() {
        Film result1 = filmStorage.createFilm(film4);

        checkFilm(result1, film4);
    }

    @Test
    @SneakyThrows
    public void testUpdateFilm() {
        Film updatedFilm = film1;
        updatedFilm.setName("Updated Name");

        Film result = filmStorage.updateFilm(updatedFilm);

        checkFilm(result, updatedFilm);
    }

    @Test
    @SneakyThrows
    public void testGetAllFilms() {
        List<Film> films = filmStorage.getAllFilms();

        assertEquals(films.size(), 3);
    }

    @Test
    @SneakyThrows
    public void testDeleteFilm() {
        Film deleteFilm = film2;
        boolean delete = filmStorage.deleteFilm(deleteFilm.getId());

        assertTrue(delete);
    }

    @Test
    @SneakyThrows
    public void testGetFilmById() {
        checkFilm(filmStorage.getFilm(1), film1);
        checkFilm(filmStorage.getFilm(2), film2);
        checkFilm(filmStorage.getFilm(3), film3);
    }


    private void checkFilm(Film result, Film expected) {
        assertThat(result.getId(), is(expected.getId()));
        assertThat(result.getName(), is(expected.getName()));
        assertThat(result.getMpa(), is(expected.getMpa()));
        assertThat(result.getDescription(), is(expected.getDescription()));
        assertThat(result.getGenres(), is(expected.getGenres()));
        assertThat(result.getReleaseDate(), is(expected.getReleaseDate()));
        assertThat(result.getDuration(), is(expected.getDuration()));
    }
}
