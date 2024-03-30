package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryUserStorage implements UserStorage {

    private static int id = 0;
    public final Map<Integer, User> users = new HashMap<>();


    @Override
    public User createUser(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
        log.info("User was create");
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("User not exist");
        }
        users.put(user.getId(), user);
        log.info("User was update");
        return user;
    }

    @Override
    public boolean deleteUser(int idUser) {
        return users.remove(idUser, users.get(idUser));
    }

    @Override
    public User getUser(int idUser) {
        return users.get(idUser);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

}
