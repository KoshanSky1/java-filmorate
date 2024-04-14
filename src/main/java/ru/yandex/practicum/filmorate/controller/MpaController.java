package ru.yandex.practicum.filmorate.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/mpa")
public class MpaController {

    private final MpaService mpaService;

    public MpaController(@Autowired(required = false) MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public ResponseEntity<List<Mpa>> findAllMpas() {
        log.info("---START FIND ALL MPAS ENDPOINT---");
        return new ResponseEntity<>(mpaService.getAllMpas(), HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping("/{idMpa}")
    public ResponseEntity<Mpa> findMpa(@PathVariable int idMpa) {
        log.info("---START FIND MPA ENDPOINT---");
        log.info("Mpa = " + mpaService.getMpa(idMpa));
        return new ResponseEntity<>(mpaService.getMpa(idMpa), HttpStatus.OK);
    }
}
