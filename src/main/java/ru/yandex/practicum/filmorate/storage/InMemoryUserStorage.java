package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements  UserStorage {
    @Getter
    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 0;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        if (users.containsValue(user)) {
            log.debug("Валидация не пройдена: такой пользователь уже существует");
            throw new ValidationException("Такой пользователь уже существует.");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) { // для тестов
            log.debug("Валидация не пройдена: email пуст или не содержит @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) { // для тестов
            log.debug("Валидация не пройдена: логин пуст или содержит пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) { // для тестов
            log.debug("Валидация не пройдена: дата рождения из будущего");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        ++this.id;
        user.setId(id);
        users.put(user.getId(), user);
        log.info("Создан новый пользователь: " + user);
        return user;
    }

    @Override
    public User put(User user) {
        if (!users.containsKey(user.getId())) {
            log.debug("Обновление невозможно: пользователь не найден");
            throw new ValidationException("Пользователь не найден.");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) { // для тестов
            log.debug("Валидация не пройдена: email пуст или не содержит @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) { // для тестов
            log.debug("Валидация не пройдена: логин пуст или содержит пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) { // для тестов
            log.debug("Валидация не пройдена: дата рождения из будущего");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь (id = " + user.getId() + ") успешно обновлён");
        return user;
    }

    @Override
    public void delete(User user) {
        if (users.containsValue(user)) {
            log.debug("Пользователь " + user.getId() + " успешно удалён");
        } else {
            log.debug("Пользователь не найден");
            throw new ValidationException("Пользователь " + user.getId() + " не найден");
        }
    }

    @Override
    public User findUserById(Integer userId) {
        User user = getUsers().get(userId);
        if (user == null) throw new UserNotFoundException(String.format("Пользователь № %d не найден", userId));
        return user;
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

}