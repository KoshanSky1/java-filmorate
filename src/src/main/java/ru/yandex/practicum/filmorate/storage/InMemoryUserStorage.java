package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
        }
        else {
            log.debug("Пользователь не найден");
            throw new ValidationException("Пользователь " + user.getId() + " не найден");
        }
    }

}