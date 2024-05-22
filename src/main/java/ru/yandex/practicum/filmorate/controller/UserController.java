package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;


import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(@Qualifier("userDbService") UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        return userService.put(user);
    }

    @DeleteMapping
    public void delete(@Valid @RequestBody User user) {
        userService.delete(user);
    }

    @GetMapping("{userId}")
    public User findUser(@PathVariable("userId") Integer userId) {
        return userService.findUserById(userId);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        userService.addAFriend(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("{id}/friends")
    public List<User> getFriendsList(@PathVariable("id") Integer id) {
        return userService.getFriendsList(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getFriendsList(@PathVariable("id") Integer id, @PathVariable("otherId") Integer otherId) {
        return userService.displayAListOfCommonFriends(id, otherId);
    }

}