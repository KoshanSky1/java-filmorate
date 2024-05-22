package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User put(User user);

    void delete(User user);

    Map<Integer, User> getUsers();

    User findUserById(Integer userId);

    void addAFriend(Integer id, Integer friendId);

    List<User> getFriendsList(Integer id);

    void deleteFriend(Integer id, Integer friendId);

    List<User> displayAListOfCommonFriends(Integer id, Integer otherId);

}