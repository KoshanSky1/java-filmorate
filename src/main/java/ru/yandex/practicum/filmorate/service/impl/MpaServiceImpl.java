package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
public class MpaServiceImpl implements MpaService {

    private final MpaStorage mpaStorage;

    @Autowired
    private MpaServiceImpl(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @Override
    public Mpa getMpa(int idMpa) {
        log.info(format("Start get idMpa = [%s]", idMpa));
        return mpaStorage.getMpa(idMpa);
    }

    @Override
    public List<Mpa> getAllMpas() {
        log.info("Start get all mpas");
        return mpaStorage.getAllMpas();
    }
}
