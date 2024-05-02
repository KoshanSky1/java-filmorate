package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikesFilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DirectorDbStorageTest {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;
    private final LikesFilmStorage likesFilmStorage;
    private final Genre genre = new Genre(1, "Комедия");
    private final Mpa mpa = new Mpa(1, "G");
    private final Director director1 = new Director(1, "Режиссер 1");
    private final Director director2 = new Director(2, "Режиссер 2");
    private final Director director3 = new Director(3, "Режиссер 3");
    private final Director director4 = new Director(3, "Режиссер 4");
    private final Film film1 = new Film(1, "God Father", "Film about father",
            LocalDate.of(1997, Month.DECEMBER, 14), 240, mpa, List.of(genre),
            List.of(director1));
    private final Film film2 = new Film(2, "God Father2", "Film about father2",
            LocalDate.of(2007, Month.DECEMBER, 14), 240, mpa, List.of(genre),
            List.of(director1));
    private final Film film3 = new Film(3, "God Father3", "Film about father3",
            LocalDate.now(), 240, mpa, List.of(genre), List.of(director2));

    private final User user1 = new User(1, "test@gmail.com", "testLogin", "Name",
            LocalDate.of(2000, 1, 1));


    @BeforeEach
    void beforeEach() {
        directorStorage.addDirectorToDatabase(director1);
        directorStorage.addDirectorToDatabase(director2);
        directorStorage.addDirectorToDatabase(director3);

        filmStorage.createFilm(film1);
        filmStorage.createFilm(film2);
        filmStorage.createFilm(film3);

        userStorage.createUser(user1);
    }

    @Test
    void getAllDirectors() {
        List<Director> directors = directorStorage.getAllDirectors();

        assertEquals(directors.size(), 3);
    }

    @Test
    void getDirectorById() {
        checkDirector(directorStorage.getDirectorById(1), director1);
        checkDirector(directorStorage.getDirectorById(2), director2);
        checkDirector(directorStorage.getDirectorById(3), director3);
    }

    @Test
    void addDirectorToDatabase() {
        Director result1 = directorStorage.addDirectorToDatabase(director4);

        checkDirector(result1, director4);
    }

    @Test
    void updateDirector() {
        Director updatedDirector = director1;
        updatedDirector.setName("Updated Name");

        Director result = directorStorage.updateDirector(updatedDirector);

        checkDirector(result, updatedDirector);
    }

    @Test
    void removeDirectorFromDatabase() {
        Director deleteDirector = director2;
        boolean delete = directorStorage.removeDirectorFromDatabase(deleteDirector.getId());

        assertTrue(delete);
    }

    @Test
    void searchFilmsByDirector() {
        List<Film> films = directorStorage.searchFilmsByDirector(1);

        assertEquals(films.size(), 2);
    }

    @Test
    void searchFilmsByDirectorSortedByYear() {
        List<Film> films = directorStorage.searchFilmsByDirectorSortedByYear(1);

        List<Film> filmsSortedByYear = new ArrayList<>();
        filmsSortedByYear.add(film1);
        filmsSortedByYear.add(film2);

        assertEquals(films, filmsSortedByYear);
    }

    @Test
    void searchFilmsByDirectorSortedByLikes() {
        likesFilmStorage.addLike(2, 1);

        List<Film> films = directorStorage.searchFilmsByDirectorSortedByLikes(1);

        List<Film> filmsSortedByYear = new ArrayList<>();
        filmsSortedByYear.add(film2);
        filmsSortedByYear.add(film1);

        assertEquals(films, filmsSortedByYear);
    }


    private void checkDirector(Director result, Director expected) {
        assertThat(result.getId(), is(expected.getId()));
        assertThat(result.getName(), is(expected.getName()));
    }
}