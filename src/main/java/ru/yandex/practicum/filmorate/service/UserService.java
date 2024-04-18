package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserService {
    Collection<User> findAll();

    User findUserById(Integer userId);

    User create(User user);

    User put(User user);

    void delete(User user);

    void addAFriend(Integer id, Integer friendId);

    List<User> getFriendsList(Integer id);

    void deleteFriend(Integer id, Integer friendId);

    List<User> displayAListOfCommonFriends(Integer id, Integer otherId);

}