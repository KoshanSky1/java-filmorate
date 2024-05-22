package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

    @Service
    public class InMemoryUserService implements UserService {
        private final UserStorage userStorage;

        @Autowired
        public InMemoryUserService(@Qualifier("inMemoryUserStorage") UserStorage userStorage) {
            this.userStorage = userStorage;
        }

        @Override
        public Collection<User> findAll() {
            return userStorage.findAll();
        }

        @Override
        public User findUserById(Integer userId) {
            return userStorage.findUserById(userId);
        }

        @Override
        public User create(User user) {
            return userStorage.create(user);
        }

        @Override
        public User put(User user) {
            return userStorage.put(user);
        }

        @Override
        public void delete(User user) {
            userStorage.delete(user);
        }

        @Override
        public void addAFriend(Integer id, Integer friendId) {
            userStorage.addAFriend(id, friendId);
        }

        @Override
        public List<User> getFriendsList(Integer id) {
            return userStorage.getFriendsList(id);
        }

        @Override
        public void deleteFriend(Integer id, Integer friendId) {
            userStorage.deleteFriend(id, friendId);
        }

        @Override
        public List<User> displayAListOfCommonFriends(Integer id, Integer otherId) {
            return userStorage.displayAListOfCommonFriends(id, otherId);
        }

}