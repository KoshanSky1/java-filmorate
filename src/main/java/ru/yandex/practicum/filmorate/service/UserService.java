package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;


    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage,
                       @Qualifier("FilmDbStorage") FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }


    public User createUser(User user) {
        log.info(format("Create user: %s", user));
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        log.info(format("Start update idUser = [%s]", user.getId()));
        return userStorage.updateUser(user);
    }

    public boolean deleteUser(int idUser) {
        return userStorage.deleteUser(idUser);
    }

    public User getUser(int idUser) {
        return userStorage.getUser(idUser);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(int idUser, int idFriend) {
        log.info(format("Start add idFriend = [%s] to idUser = [%s] friends", idFriend, idUser));
        User user = getUser(idUser);
        User friend = getUser(idFriend);
        userStorage.addFriend(user.getId(), friend.getId());
        log.info(format("Success add idFriend = [%s] to idUser = [%s] friends", idFriend, idUser));
    }

    public void deleteFriend(int idUser, int idFriend) {
        log.info(format("Start delete idFriend = [%s] from idUser = [%s] friends", idFriend, idUser));
        User user = getUser(idUser);
        User friend = getUser(idFriend);
        userStorage.deleteFriend(user.getId(), friend.getId());
        log.info(format("Success delete idFriend = [%s] from idUser = [%s] friends", idFriend, idUser));
    }

    public List<User> findCommonFriends(int idUser, int idFriend) {
        log.info(format("Start find common friend idUser = [%s] and idFriend = [%s]", idUser, idFriend));
        User user = getUser(idUser);
        User friend = getUser(idFriend);
        log.info(format("Common friends list size: " + userStorage.getCommonFriends(user.getId(), friend.getId()).size()));
        return userStorage.getCommonFriends(idUser, idFriend);
    }

    public List<User> getFriends(int idUser) {
        log.info(format("Start get idUser = [%s] friends", idUser));
        User user = getUser(idUser);
        log.info("Friend list size: " + userStorage.getFriends(user.getId()).size());
        return userStorage.getFriends(idUser);
    }

    public List<Film> getRecommendations(int idUser) {
        log.info(format("Start get film's recommendations for idUser = [%s]", idUser));
        User user = getUser(idUser);
        return filmStorage.getRecommendations(user.getId());
    }
}
