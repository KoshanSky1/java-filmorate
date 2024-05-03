package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.feed.Event;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User updateUser(User user);

    boolean deleteUser(int idUser);

    User getUser(int idUser);

    List<User> getAllUsers();

    void addFriend(int idUser, int idFriend);

    void deleteFriend(int idUser, int idFriend);

    List<User> findCommonFriends(int idUser, int idFriend);

    List<User> getFriends(int idUser);

    List<Film> getRecommendations(int idUser);

    List<Event> getFeed(int idUser);
}
