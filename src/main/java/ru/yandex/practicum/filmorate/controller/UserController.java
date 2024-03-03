package ru.yandex.practicum.filmorate.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class UserController {

    private static int id = 0;
    public final Map<Integer, User> users = new HashMap<>();

    @SneakyThrows
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        log.debug("user = " + user);
        if ((user.getName() == null) || (user.getName().isEmpty())) {
            log.info("User has null or empty name");
            user.setName(user.getLogin());
        }
        user.setId(++id);
        users.put(user.getId(), user);
        log.info("User was create");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @SneakyThrows
    @PutMapping
    public ResponseEntity<?> updateUser(@Valid @RequestBody User user) {
        if ((user.getName() == null) || (user.getName().isEmpty())) {
            user.setName(user.getLogin());
        }
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("User not exist");
        }
        users.put(user.getId(), user);
        log.info("User was update");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return new ResponseEntity<>(new ArrayList<>(users.values()), HttpStatus.OK);
    }
}
