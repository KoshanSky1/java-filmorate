package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.InMemoryFilmService;
import ru.yandex.practicum.filmorate.service.InMemoryUserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidationTest {
    private final InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
    private final InMemoryUserStorage userStorage = new InMemoryUserStorage();
    private final InMemoryUserService userService = new InMemoryUserService(userStorage);
    private final InMemoryFilmService filmService = new InMemoryFilmService(filmStorage);
    private final FilmController filmController = new FilmController(filmService);
    private final Film filmNumberOne = Film.builder()
            .name("Титаник")
            .description("Американская эпическая романтическая драма")
            .releaseDate(LocalDate.of(1997, Month.DECEMBER, 14))
            .duration(154L)
            .build();

    @BeforeEach
    protected void addTestFilm() {
        filmController.create(filmNumberOne);
    }

    @Test
    public void create() {
        final Collection<Film> films = filmController.findAll();

        assertNotNull(filmNumberOne, "Фильм не найден.");;
        assertNotNull(films, "Фильмы на возвращаются.");
        assertEquals(1, films.size(), "Неверное количество фильмов.");
        assertEquals(filmNumberOne.getId(), 1, "Идентификаторы не совпадают.");
    }

    @Test
    public void createAnExistingFilm() {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.create(filmNumberOne);
                    }
                });

        assertEquals("Такой фильм уже существует.", exception.getMessage());
        final Collection<Film> films = filmController.findAll();
        assertEquals(1, films.size(), "Изменилось количество фильмов.");
    }

    @Test
    public void createWithEmptyFilmName() {
        Film testFilm = Film.builder()
                .name("   ")
                .description("Test description")
                .releaseDate(LocalDate.of(2000, Month.DECEMBER, 26))
                .duration(180L)
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                       filmController.create(testFilm);
                    }
                });

        assertEquals("Название фильма не может быть пустым.", exception.getMessage());
        final Collection<Film> films = filmController.findAll();
        assertEquals(1, films.size(), "Изменилось количество фильмов.");
    }

    @Test
    public void createWithADescriptionExceeding200Characters() {
        Film testFilm = Film.builder()
                .name("Test")
                .description("TestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTest" +
                        "TestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTest" +
                        "TestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTest")
                .releaseDate(LocalDate.of(2000, Month.DECEMBER, 26))
                .duration(180L)
                .build();

        final ValidationException exception = assertThrows(
            ValidationException.class,
            new Executable() {
                @Override
                public void execute() {
                    filmController.create(testFilm);
                }
            });

        assertEquals("Максимальная длина описания фильма — 200 символов.", exception.getMessage());
        final Collection<Film> films = filmController.findAll();
        assertEquals(1, films.size(), "Изменилось количество фильмов.");
    }

    @Test
    public void createWithAReleaseDateFromThePast() {
        Film testFilm = Film.builder()
                .name("Test")
                .description("Test description")
                .releaseDate(LocalDate.of(1700, Month.DECEMBER, 14))
                .duration(180L)
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.create(testFilm);
                    }
                });

        assertEquals("Дата релиза фильма должна быть не раньше 28 декабря 1895 года.", exception.getMessage());
        final Collection<Film> films = filmController.findAll();
        assertEquals(1, films.size(), "Изменилось количество фильмов.");
    }

    @Test
    public void createWithNegativeDuration() {
        Film testFilm = Film.builder()
                .name("Test")
                .description("Test description")
                .releaseDate(LocalDate.of(1999, Month.DECEMBER, 14))
                .duration(-180L)
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.create(testFilm);
                    }
                });

        assertEquals("Продолжительность фильма должна быть положительной.", exception.getMessage());
        final Collection<Film> films = filmController.findAll();
        assertEquals(1, films.size(), "Изменилось количество фильмов.");
    }

    @Test
    public void put() {
        Film updatedFilm = Film.builder()
                .name("Титаник")
                .description("Американская эпическая романтическая драма и фильм катастрофа")
                .releaseDate(LocalDate.of(1997, Month.DECEMBER, 14))
                .duration(184L)
                .build();

        updatedFilm.setId(filmNumberOne.getId());
        filmController.put(updatedFilm);

        final Collection<Film> films = filmController.findAll();

        assertNotNull(updatedFilm, "Фильм не найден.");
        assertNotNull(films, "Фильмы на возвращаются.");
        assertEquals(1, films.size(), "Изменилось количество фильмов.");
        assertEquals(updatedFilm.getId(), 1, "Идентификаторы не совпадают.");
    }

    @Test
    public void putWithNonExistentId() {
        Film updatedFilm = Film.builder()
                .name("Титаник")
                .description("Американская эпическая романтическая драма и фильм катастрофа")
                .releaseDate(LocalDate.of(1997, Month.DECEMBER, 14))
                .duration(184L)
                .build();

        updatedFilm.setId(9999);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.put(updatedFilm);
                    }
                });

        assertEquals("Фильм не найден.", exception.getMessage());
        final Collection<Film> films = filmController.findAll();
        assertEquals(1, films.size(), "Изменилось количество фильмов.");
    }

    @Test
    public void putWithEmptyFilmName() {
        Film testFilm = Film.builder()
                .name("   ")
                .description("Test description")
                .releaseDate(LocalDate.of(1997, Month.DECEMBER, 14))
                .duration(184L)
                .build();

        testFilm.setId(filmNumberOne.getId());

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.put(testFilm);
                    }
                });

        assertEquals("Название фильма не может быть пустым.", exception.getMessage());
        final Collection<Film> films = filmController.findAll();
        assertEquals(1, films.size(), "Изменилось количество фильмов.");
    }

    @Test
    public void putWithADescriptionExceeding200Characters() {
        Film testFilm = Film.builder()
                .name("Test")
                .description("TestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTest" +
                        "TestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTest" +
                        "TestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTest")
                .releaseDate(LocalDate.of(2000, Month.DECEMBER, 26))
                .duration(180L)
                .build();

        testFilm.setId(filmNumberOne.getId());

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.put(testFilm);
                    }
                });

        assertEquals("Максимальная длина описания фильма — 200 символов.", exception.getMessage());
        final Collection<Film> films = filmController.findAll();
        assertEquals(1, films.size(), "Изменилось количество фильмов.");
    }

    @Test
    public void putWithAReleaseDateFromThePast() {
        Film testFilm = Film.builder()
                .name("Test")
                .description("Test description")
                .releaseDate(LocalDate.of(1700, Month.DECEMBER, 14))
                .duration(184L)
                .build();

        testFilm.setId(filmNumberOne.getId());

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.put(testFilm);
                    }
                });

        assertEquals("Дата релиза фильма должна быть не раньше 28 декабря 1895 года.", exception.getMessage());
        final Collection<Film> films = filmController.findAll();
        assertEquals(1, films.size(), "Изменилось количество фильмов.");
    }

    @Test
    public void putWithNegativeDuration() {
        Film testFilm = Film.builder()
                .name("Test")
                .description("Test description")
                .releaseDate(LocalDate.of(2008, Month.DECEMBER, 14))
                .duration(-184L)
                .build();

        testFilm.setId(filmNumberOne.getId());

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        filmController.put(testFilm);
                    }
                });

        assertEquals("Продолжительность фильма должна быть положительной.", exception.getMessage());
        final Collection<Film> films = filmController.findAll();
        assertEquals(1, films.size(), "Изменилось количество фильмов.");
    }

}