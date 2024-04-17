package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MpaDbStorageTest {
    private final MpaStorage mpaStorage;

    @Test
    @SneakyThrows
    public void getRatingList() {
        List<Mpa> mpaList = mpaStorage.getAllMpas();

        checkMpa(mpaList.get(0));
        checkMpa(mpaList.get(1));
        checkMpa(mpaList.get(2));
        checkMpa(mpaList.get(3));
        checkMpa(mpaList.get(4));
    }

    @Test
    @SneakyThrows
    public void getRatingById() {
        checkMpa(mpaStorage.getMpa(1));
    }

    @Test
    @SneakyThrows
    public void deleteGenre() {
        List<Mpa> mpaList = mpaStorage.getAllMpas();

        mpaStorage.deleteMpa(3);
        List<Mpa> mpaListNew = mpaStorage.getAllMpas();

        assertEquals(mpaListNew.size(), (mpaList.size() - 1));
    }

    private void checkMpa(Mpa mpa) {
        int id = mpa.getId();
        String name = mpa.getName();
        String expectedName;

        switch (id) {
            case 1:
                expectedName = "G";
                break;
            case 2:
                expectedName = "PG";
                break;
            case 3:
                expectedName = "PG-13";
                break;
            case 4:
                expectedName = "R";
                break;
            case 5:
                expectedName = "NC-17";
                break;
            default:
                expectedName = "";
        }

        assertThat(name, is(expectedName));
    }
}
