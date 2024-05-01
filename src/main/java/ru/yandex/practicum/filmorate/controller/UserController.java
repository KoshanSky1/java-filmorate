package ru.yandex.practicum.filmorate.controller;

import lombok.NonNull;
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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    public UserController(@Autowired(required = false) UserService userService) {
        this.userService = userService;
    }


    @SneakyThrows
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.info("---START CREATE USER ENDPOINT---");
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.OK);
    }

    @SneakyThrows
    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.info("---START UPDATE USER ENDPOINT---");
        return new ResponseEntity<>(userService.updateUser(user), HttpStatus.OK);
    }

    @SneakyThrows
    @DeleteMapping("/{idUser}")
    public ResponseEntity<?> deleteUser(@PathVariable int idUser) {
        log.info("---START DELETE USER ENDPOINT---");
        if (userService.deleteUser(idUser)) {
            return new ResponseEntity<>(new SuccessJSON("User was delete").toString(), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ErrorResponse("Error"), HttpStatus.BAD_REQUEST);
    }

    @SneakyThrows
    @GetMapping("/{idUser}")
    public ResponseEntity<User> findUser(@PathVariable int idUser) {
        log.info("---START FIND USER ENDPOINT---");
        log.info("User = " + userService.getUser(idUser));
        return new ResponseEntity<>(userService.getUser(idUser), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<User>> findAllUsers() {
        log.info("---START FIND ALL USERS ENDPOINT---");
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @SneakyThrows
    @PutMapping("/{idUser}/friends/{idFriend}")
    public ResponseEntity<SuccessJSON> addFriend(@NonNull @PathVariable int idUser,
                                                 @NonNull @PathVariable int idFriend) {
        log.info("---START ADD FRIEND ENDPOINT---");
        userService.addFriend(idUser, idFriend);
        return new ResponseEntity<>(new SuccessJSON("User add to friends"), HttpStatus.OK);
    }

    @SneakyThrows
    @DeleteMapping("/{idUser}/friends/{idFriend}")
    public ResponseEntity<SuccessJSON> deleteFriend(@NonNull @PathVariable int idUser,
                                                    @NonNull @PathVariable int idFriend) {
        log.info("---START DELETE FRIEND ENDPOINT---");
        userService.deleteFriend(idUser, idFriend);
        return new ResponseEntity<>(new SuccessJSON("User was delete from friends"), HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping("/{idUser}/friends/common/{idFriend}")
    public ResponseEntity<List<User>> findCommonFriends(@NonNull @PathVariable int idUser,
                                                        @NonNull @PathVariable int idFriend) {
        log.info("---START FIND COMMON FRIENDS ENDPOINT---");
        return new ResponseEntity<>(userService.findCommonFriends(idUser, idFriend), HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping("/{idUser}/friends")
    public ResponseEntity<List<User>> findFriends(@NonNull @PathVariable int idUser) {
        log.info("---START FIND FRIENDS ENDPOINT---");
        return new ResponseEntity<>(userService.getFriends(idUser), HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping("/{idUser}/recommendations")
    public ResponseEntity<List<Film>> findRecommendations(@NonNull @PathVariable int idUser) {
        log.info("---START FIND RECOMMENDATIONS FILMS ENDPOINT---");
        return new ResponseEntity<>(userService.getRecommendations(idUser), HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping("/{idUser}/feed")
    public ResponseEntity<List<Event>> getFeed(@NonNull @PathVariable int idUser) {
        log.info("---START FIND FEED ENDPOINT---");
        return new ResponseEntity<>(userService.getFeed(idUser), HttpStatus.OK);
    }
}
