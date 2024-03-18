package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;


    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }


    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        log.info(format("Start update idUser = %s", user.getId()));
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
        user.addFriend(friend.getId());
        friend.addFriend(user.getId());
        updateUser(user);
        updateUser(friend);
        log.info(format("Success add idFriend = [%s] to idUser = [%s] friends", idFriend, idUser));
    }

    public void deleteFriend(int idUser, int idFriend) {
        log.info(format("Start delete idFriend = [%s] from idUser = [%s] friends", idFriend, idUser));
        User user = getUser(idUser);
        User friend = getUser(idFriend);
        user.deleteFriend(idFriend);
        friend.deleteFriend(idUser);
        updateUser(user);
        updateUser(friend);
        log.info(format("Success delete idFriend = [%s] from idUser = [%s] friends", idFriend, idUser));
    }

    public List<User> findCommonFriends(int idUser, int idFriend) {
        log.info(format("Start find common friend idUser = [%s] and idFriend = [%s]", idUser, idFriend));
        User user = getUser(idUser);
        User friend = getUser(idFriend);
        List<User> commonFriends = new ArrayList<>();
        for (Integer id : user.getFriends()) {
            if (friend.getFriends().contains(id)) {
                commonFriends.add(getUser(id));
            }
        }
        log.info(format("Common friends list size: " + commonFriends.size()));
        return commonFriends;
    }

    public List<User> getFriends(int idUser) {
        log.info(format("Start get idUser = [%s] friends", idUser));
        User user = getUser(idUser);
        List<User> friends = new ArrayList<>();
        for (Integer id : user.getFriends()) {
            friends.add(getUser(id));
        }
        log.info("Friend list size: " + friends.size());
        return friends;
    }
}
