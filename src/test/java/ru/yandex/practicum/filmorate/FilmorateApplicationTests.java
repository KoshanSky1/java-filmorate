package ru.yandex.practicum.filmorate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.*;

@SpringBootTest
class FilmorateApplicationTests {
    @Autowired
    private FilmController filmController;

    @Autowired
    private GenreController genreController;

    @Autowired
    private RatingController ratingController;

    @Autowired
    private UserController userController;

    @Test
    public void contextLoads() {
        Assertions.assertThat(filmController).isNotNull();
        Assertions.assertThat(genreController).isNotNull();
        Assertions.assertThat(ratingController).isNotNull();
        Assertions.assertThat(userController).isNotNull();
    }

}