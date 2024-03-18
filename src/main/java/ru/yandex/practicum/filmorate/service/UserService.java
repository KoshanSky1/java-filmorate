package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {
    private final InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findUserById(Integer userId) {
        User user = userStorage.getUsers().get(userId);
        if (user == null) throw new UserNotFoundException(String.format("Пользователь № %d не найден", userId));
        return user;
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
        User user = getUsers().get(id);
        User friend = getUsers().get(friendId);
        if (user == null) {
            log.debug(String.format("Пользователь № %d не найден", id));
            throw new UserNotFoundException(String.format("Пользователь № %d не найден", id));
        }
        if (friend == null) {
            log.debug(String.format("Пользователь № %d не найден", friendId));
            throw new UserNotFoundException(String.format("Пользователь № %d не найден", friendId));
        }
            user.getFriends().add(friendId);
            friend.getFriends().add(id);
            log.info("Пользователь №" + id + " добавил в друзья пользователя №" + friendId);
    }

   public List<User> getFriendsList(Integer id) {
       if (getUsers().get(id) != null) {
           Set<Integer> friendsId = getUsers().get(id).getFriends();
           List<User> friends = new ArrayList<>();
           for (int userId : friendsId) {
               friends.add(getUsers().get(userId));
           }
           log.info("Сформирован список друзей для пользователя №" + id);
           return friends;
       } else {
           log.debug(String.format("Пользователь № %d не найден", id));
           throw new UserNotFoundException(String.format("Пользователь № %d не найден", id));
       }
   }

    public void deleteFriend(Integer id, Integer friendId) {
        User user = getUsers().get(id);
        User friend = getUsers().get(friendId);
        if (user == null) {
            log.debug(String.format("Пользователь № %d не найден", id));
            throw new UserNotFoundException(String.format("Пользователь № %d не найден", id));
        }
        if (friend == null) {
            log.debug(String.format("Пользователь № %d не найден", friendId));
            throw new UserNotFoundException(String.format("Пользователь № %d не найден", friendId));
        }
            user.getFriends().remove(friendId);
            friend.getFriends().remove(id);
            log.info("Пользователь №" + id + " удалил удалил друга №" + friendId);
    }

    public List<User> displayAListOfCommonFriends(Integer id, Integer otherId) {
        User user = getUsers().get(id);
        User otherUser = getUsers().get(otherId);
        List<User> commonFriends = new ArrayList<>();
        if (user == null) {
            log.debug(String.format("Пользователь № %d не найден", id));
            throw new UserNotFoundException(String.format("Пользователь № %d не найден", id));
        }
        if (otherUser == null) {
            log.debug(String.format("Пользователь № %d не найден", otherId));
            throw new UserNotFoundException(String.format("Пользователь № %d не найден", otherId));
        }
        for (Integer userId : getUsers().get(id).getFriends()) {
            if (getUsers().get(otherId).getFriends().contains(userId)) {
                commonFriends.add(getUsers().get(userId));
            }
        }

        log.info("Сформирован список общих друзей для пользователей №" + id + " и №" + otherId);
        return commonFriends;
    }

    public Map<Integer, User> getUsers() {
        return userStorage.getUsers();
    }

}