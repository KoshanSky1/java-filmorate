package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User getUser(int idUser);

    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    boolean deleteUser(int idUser);
}
