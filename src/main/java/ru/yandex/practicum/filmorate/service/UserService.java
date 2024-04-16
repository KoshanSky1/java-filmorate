package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findUserById(Integer userId) {
        return userStorage.findUserById(userId);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User put(User user) {
        return userStorage.put(user);
    }

    public void delete(User user) {
        userStorage.delete(user);
    }

    public void addAFriend(Integer id, Integer friendId) {
        userStorage.addAFriend(id, friendId);
    }

    public List<User> getFriendsList(Integer id) {
        return userStorage.getFriendsList(id);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        userStorage.deleteFriend(id, friendId);
    }

    public List<User> displayAListOfCommonFriends(Integer id, Integer otherId) {
        return userStorage.displayAListOfCommonFriends(id, otherId);
    }

    public Map<Integer, User> getUsers() {
        return userStorage.getUsers();
    }

}