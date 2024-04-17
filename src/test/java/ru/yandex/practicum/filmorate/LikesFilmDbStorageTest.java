package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikesFilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LikesFilmDbStorageTest {

    private final LikesFilmStorage likesFilmStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final Genre genre = new Genre(1, "Комедия");
    private final Mpa mpa = new Mpa(1, "G");
    private final Film film1 = new Film(1, "God Father", "Film about father",
            LocalDate.now(), 240, mpa, List.of(genre));
    private final Film film2 = new Film(2, "God Father2", "Film about father2",
            LocalDate.now(), 240, mpa, List.of(genre));
    private final Film film3 = new Film(3, "God Father3", "Film about father3",
            LocalDate.now(), 240, mpa, List.of(genre));
    private final User user1 = new User(1, "test@gmail.com", "testLogin", "Name", LocalDate.of(2000, 1, 1));
    private final User user2 = new User(2, "test@gmail.com", "testLogin", "Name", LocalDate.of(2000, 1, 1));

    @Test
    @SneakyThrows
    public void testLikeFilm() {
        filmStorage.createFilm(film1);
        filmStorage.createFilm(film2);
        filmStorage.createFilm(film3);

        userStorage.createUser(user1);
        userStorage.createUser(user2);

        likesFilmStorage.addLike(film1.getId(), user1.getId());
        likesFilmStorage.addLike(film3.getId(), user2.getId());
        likesFilmStorage.addLike(film3.getId(), user1.getId());
    }

    @Test
    @SneakyThrows
    public void testUnlikeFilm() {
        testLikeFilm();

        likesFilmStorage.deleteLike(film3.getId(), user2.getId());
        likesFilmStorage.deleteLike(film3.getId(), user1.getId());

        List<Film> result = likesFilmStorage.getPopularFilms(2);

        assertThat(result.get(0), is(film1));
        assertThat(result.get(1), is(film3));
    }

    @Test
    public void testGetMostLikedFilmsWithLimit() {
        testLikeFilm();

        List<Film> result = likesFilmStorage.getPopularFilms(2);

        assertThat(result.get(0), is(film3));
        assertThat(result.get(1), is(film1));
    }

}
