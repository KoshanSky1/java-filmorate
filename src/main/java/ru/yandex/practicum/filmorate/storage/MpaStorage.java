package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {
    List<Mpa> getAllMpas();

    Mpa getMpa(int idMpa);

    Mpa createMpa(Mpa mpa);

    Mpa updateMpa(Mpa mpa);

    boolean deleteMpa(int idMpa);

}
