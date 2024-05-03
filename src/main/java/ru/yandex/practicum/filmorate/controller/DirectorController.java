package ru.yandex.practicum.filmorate.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exeption.ErrorResponse;
import ru.yandex.practicum.filmorate.json.SuccessJSON;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/directors")
public class DirectorController {

    private final DirectorService directorService;

    public DirectorController(@Autowired(required = false) DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public ResponseEntity<List<Director>> findAllDirectors() {
        log.info("---START FIND ALL DIRECTORS ENDPOINT---");
        return new ResponseEntity<>(directorService.getAllDirectors(), HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping("/{idDirector}")
    public ResponseEntity<Director> findDirector(@PathVariable int idDirector) {
        log.info("---START FIND DIRECTOR ENDPOINT---");
        log.info("Director = " + directorService.getDirectorById(idDirector).toString());
        return new ResponseEntity<>(directorService.getDirectorById(idDirector), HttpStatus.OK);
    }

    @SneakyThrows
    @PostMapping
    public ResponseEntity<Director> addDirectorToDatabase(@Valid @RequestBody Director director) {
        log.info("---START CREATE DIRECTOR ENDPOINT---");
        return new ResponseEntity<>(directorService.addDirectorToDB(director), HttpStatus.OK);
    }

    @SneakyThrows
    @PutMapping
    public ResponseEntity<Director> updateDirector(@Valid @RequestBody Director director) {
        log.info("---START UPDATE DIRECTOR ENDPOINT---");
        return new ResponseEntity<>(directorService.updateDirector(director), HttpStatus.OK);
    }

    @SneakyThrows
    @DeleteMapping("/{idDirector}")
    public ResponseEntity<?> deleteDirector(@PathVariable int idDirector) {
        log.info("---START DELETE DIRECTOR ENDPOINT---");
        if (directorService.removeDirectorFromDB(idDirector)) {
            return new ResponseEntity<>(new SuccessJSON("Director was delete"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse("Error"), HttpStatus.BAD_REQUEST);
    }

}