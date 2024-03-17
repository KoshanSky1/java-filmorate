package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserValidationTest {
    private final InMemoryUserStorage userStorage = new InMemoryUserStorage();
    private final UserService userService = new UserService(userStorage);
    private final UserController userController = new UserController(userService);
    private final User userNumberOne = User.builder()
            .email("koshansky@mail.ru")
            .login("koshansky")
            .name("Галина")
            .birthday(LocalDate.of(1993, Month.MAY, 8))
            .build();

    @BeforeEach
    protected void addTestUser() {
        userController.create(userNumberOne);
    }

    @Test
    void create() {
        final Collection<User> users = userController.findAll();

        assertNotNull(userNumberOne, "Пользователь не найден.");
        assertNotNull(users, "Пользователи на возвращаются.");
        assertEquals(1, users.size(), "Неверное количество пользователей.");
        assertEquals(userNumberOne.getId(), 1, "Идентификаторы не совпадают.");
    }

    @Test
    void createAnExistingUser() {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.create(userNumberOne);
                    }
                });

        assertEquals("Такой пользователь уже существует.", exception.getMessage());
        final Collection<User> users = userController.findAll();
        assertEquals(1, users.size(), "Изменилось количество пользователей.");
    }

    @Test
    void createWithEmptyEmail() {
        User testUser = User.builder()
                .email("   ")
                .login("Test")
                .name("Test")
                .birthday(LocalDate.of(2021, Month.MAY, 13))
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.create(testUser);
                    }
                });

        assertEquals("Электронная почта не может быть пустой и должна содержать символ @.", exception.getMessage());
        final Collection<User> users = userController.findAll();
        assertEquals(1, users.size(), "Изменилось количество пользователей.");
    }

    @Test
    void createWithoutEmailSymbol() {
        User testUser = User.builder()
                .email("test.ru")
                .login("Test")
                .name("Test")
                .birthday(LocalDate.of(2021, Month.MAY, 13))
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.create(testUser);
                    }
                });

        assertEquals("Электронная почта не может быть пустой и должна содержать символ @.", exception.getMessage());
        final Collection<User> users = userController.findAll();
        assertEquals(1, users.size(), "Изменилось количество пользователей.");
    }

    @Test
    void createWithEmptyLogin() {
        User testUser = User.builder()
                .email("test@mail.ru")
                .login("   ")
                .name("Test")
                .birthday(LocalDate.of(2021, Month.MAY, 13))
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.create(testUser);
                    }
                });

        assertEquals("Логин не может быть пустым и содержать пробелы.", exception.getMessage());
        final Collection<User> users = userController.findAll();
        assertEquals(1, users.size(), "Изменилось количество пользователей.");
    }

    @Test
    void createWithLoginContainsSpaces() {
        User testUser = User.builder()
                .email("test@mail.ru")
                .login("Test test")
                .name("Test")
                .birthday(LocalDate.of(2021, Month.MAY, 13))
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.create(testUser);
                    }
                });

        assertEquals("Логин не может быть пустым и содержать пробелы.", exception.getMessage());
        final Collection<User> users = userController.findAll();
        assertEquals(1, users.size(), "Изменилось количество пользователей.");
    }

    @Test
    void createWithEmptyName() {
        User testUser = User.builder()
                .email("test@mail.ru")
                .login("Test")
                .name("   ")
                .birthday(LocalDate.of(2021, Month.MAY, 13))
                .build();

        userController.create(testUser);

        assertEquals(testUser.getName(), testUser.getLogin());
        final Collection<User> users = userController.findAll();
        assertEquals(2, users.size(), "Изменилось количество пользователей.");
    }

    @Test
    void createWithBirthdayFromTheFuture() {
        User testUser = User.builder()
                .email("test@mail.ru")
                .login("Test")
                .name("Test")
                .birthday(LocalDate.of(2028, Month.MAY, 13))
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.create(testUser);
                    }
                });

        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
        final Collection<User> users = userController.findAll();
        assertEquals(1, users.size(), "Изменилось количество пользователей.");
    }

    @Test
    void put() {
        User updatedUser = User.builder()
                .email("test@mail.ru")
                .login("Test")
                .name("Test")
                .birthday(LocalDate.of(2000, Month.MAY, 13))
                .build();

        updatedUser.setId(userNumberOne.getId());
        userController.put(updatedUser);

        final Collection<User> users = userController.findAll();
        assertNotNull(updatedUser, "Пользователь не найден.");
        assertNotNull(users, "Пользователи на возвращаются.");
        assertEquals(1, users.size(), "Изменилось количество пользователей.");
        assertEquals(updatedUser.getId(), 1, "Идентификаторы не совпадают.");
    }

    @Test
    void putWithNonExistentId() {
        User updatedUser = User.builder()
                .email("test@mail.ru")
                .login("Test")
                .name("Test")
                .birthday(LocalDate.of(2000, Month.MAY, 13))
                .build();

        updatedUser.setId(9999);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.put(updatedUser);
                    }
                });

        assertEquals("Пользователь не найден.", exception.getMessage());
        final Collection<User> users = userController.findAll();
        assertEquals(1, users.size(), "Изменилось количество пользователей.");
    }

    @Test
    void putWithAnEmptyEmail() {
        User updatedUser = User.builder()
                .email("   ")
                .login("Test")
                .name("Test")
                .birthday(LocalDate.of(2000, Month.MAY, 13))
                .build();

        updatedUser.setId(userNumberOne.getId());

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.put(updatedUser);
                    }
                });

        assertEquals("Электронная почта не может быть пустой и должна содержать символ @.", exception.getMessage());
        final Collection<User> users = userController.findAll();
        assertEquals(1, users.size(), "Изменилось количество пользователей.");
    }

    @Test
    void putWithoutEmailSymbol() {
        User updatedUser = User.builder()
                .email("test.ru")
                .login("Test")
                .name("Test")
                .birthday(LocalDate.of(2000, Month.MAY, 13))
                .build();

        updatedUser.setId(userNumberOne.getId());

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.put(updatedUser);
                    }
                });

        assertEquals("Электронная почта не может быть пустой и должна содержать символ @.", exception.getMessage());
        final Collection<User> users = userController.findAll();
        assertEquals(1, users.size(), "Изменилось количество пользователей.");
    }

    @Test
    void putWithAnEmptyLogin() {
        User updatedUser = User.builder()
                .email("test@yandex.ru")
                .login("   ")
                .name("Test")
                .birthday(LocalDate.of(2000, Month.MAY, 13))
                .build();

        updatedUser.setId(userNumberOne.getId());

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.put(updatedUser);
                    }
                });

        assertEquals("Логин не может быть пустым и содержать пробелы.", exception.getMessage());
        final Collection<User> users = userController.findAll();
        assertEquals(1, users.size(), "Изменилось количество пользователей.");
    }

    @Test
    void putWithAnLoginContainsSpaces() {
        User updatedUser = User.builder()
                .email("test@yandex.ru")
                .login("Test test")
                .name("Test")
                .birthday(LocalDate.of(2000, Month.MAY, 13))
                .build();

        updatedUser.setId(userNumberOne.getId());

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.put(updatedUser);
                    }
                });

        assertEquals("Логин не может быть пустым и содержать пробелы.", exception.getMessage());
        final Collection<User> users = userController.findAll();
        assertEquals(1, users.size(), "Изменилось количество пользователей.");
    }

    @Test
    void putWithEmptyName() {
        User updatedUser = User.builder()
                .email("test@mail.ru")
                .login("Test")
                .name("   ")
                .birthday(LocalDate.of(2000, Month.MAY, 13))
                .build();

        updatedUser.setId(userNumberOne.getId());
        userController.put(updatedUser);

        assertEquals(updatedUser.getName(), updatedUser.getLogin());
        final Collection<User> users = userController.findAll();
        assertEquals(1, users.size(), "Изменилось количество пользователей.");
    }

    @Test
    void putWithBirthdayFromTheFuture() {
        User updatedUser = User.builder()
                .email("test@mail.ru")
                .login("Test")
                .name("Test")
                .birthday(LocalDate.of(2030, Month.MAY, 13))
                .build();

        updatedUser.setId(userNumberOne.getId());

        final ValidationException exception = assertThrows(
                ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        userController.put(updatedUser);
                    }
                });

        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
        final Collection<User> users = userController.findAll();
        assertEquals(1, users.size(), "Изменилось количество пользователей.");
    }

}