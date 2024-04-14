package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
public class MpaService {

    private final MpaStorage mpaStorage;

    @Autowired
    private MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Mpa getMpa(int idMpa) {
        log.info(format("Start get idMpa = [%s]", idMpa));
        return mpaStorage.getMpa(idMpa);
    }

    public List<Mpa> getAllMpas() {
        log.info("Start get all mpas");
        return mpaStorage.getAllMpas();
    }
}
