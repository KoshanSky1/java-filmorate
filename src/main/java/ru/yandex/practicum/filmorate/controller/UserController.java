package ru.yandex.practicum.filmorate.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    public final Map<Integer, User> users = new HashMap<>();

    @SneakyThrows
    @PostMapping(value = "/user")
    public ResponseEntity<?> createUser(@RequestBody @Valid User user) {
        if ((user.getName() == null) || (user.getName().isEmpty())){
            log.info("User has null or empty name");
            user.setName(user.getLogin());
        }
        log.info("User was create");
        users.put(user.getId(), user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @SneakyThrows
    @PutMapping(value = "/user")
    public ResponseEntity<?> updateUser(@RequestBody @Valid User user) {
        if ((user.getName() == null) || (user.getName().isEmpty())){
            user.setName(user.getLogin());
        }
        log.info("User was update");
        users.put(user.getId(), user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return new ResponseEntity<>(new ArrayList<>(users.values()), HttpStatus.OK);
    }
}
