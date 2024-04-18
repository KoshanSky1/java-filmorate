package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void findAll() {
        User userNumberOne = User.builder()
                .email("test@mail.ru")
                .login("test")
                .name("Тeст")
                .birthday(LocalDate.of(1993, Month.MAY, 8))
                .build();

        User userNumberTwo = User.builder()
                .email("test2@mail.ru")
                .login("test2")
                .name("Тeст2")
                .birthday(LocalDate.of(2018, Month.FEBRUARY, 8))
                .build();

        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        userNumberOne = userDbStorage.create(userNumberOne);
        userNumberTwo = userDbStorage.create(userNumberTwo);

        Map<Integer, User> users = new HashMap<>();
        users.put(1, userNumberOne);
        users.put(2, userNumberTwo);

        Collection<User> savedUsers = userDbStorage.findAll();

        assertThat(savedUsers)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(users.values());
    }

    @Test
    public void create() {
        User userNumberOne = User.builder()
                .email("test@mail.ru")
                .login("test")
                .name("Тeст")
                .birthday(LocalDate.of(1993, Month.MAY, 8))
                .build();

        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        userNumberOne = userDbStorage.create(userNumberOne);

        User savedUser = userDbStorage.findUserById(userNumberOne.getId());

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(userNumberOne);
    }

    @Test
    public void put() {
        User userNumberOne = User.builder()
                .email("test@mail.ru")
                .login("test")
                .name("Тeст")
                .birthday(LocalDate.of(1993, Month.MAY, 8))
                .build();

        User userNumberOneUpdated = User.builder()
                .email("test@mail.ru")
                .login("Обновленный логин")
                .name("Обновленное имя")
                .birthday(LocalDate.of(1993, Month.MAY, 8))
                .build();

        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        userNumberOne = userDbStorage.create(userNumberOne);
        userNumberOneUpdated.setId(userNumberOne.getId());
        userDbStorage.put(userNumberOneUpdated);

        User savedUser = userDbStorage.findUserById(userNumberOne.getId());

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(userNumberOneUpdated);
    }

    @Test
    public void delete() {
        User userNumberOne = User.builder()
                .email("test@mail.ru")
                .login("test")
                .name("Тeст")
                .birthday(LocalDate.of(1993, Month.MAY, 8))
                .build();

        User userNumberTwo = User.builder()
                .email("test2@mail.ru")
                .login("test2")
                .name("Тeст2")
                .birthday(LocalDate.of(2000, Month.APRIL, 15))
                .build();

        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        userDbStorage.create(userNumberOne);
        userDbStorage.create(userNumberTwo);
        userDbStorage.delete(userNumberOne);

        Map<Integer, User> users = new HashMap<>();
        users.put(1, userNumberOne);
        users.put(2, userNumberTwo);
        users.remove(1);

        Collection<User> savedUsers = userDbStorage.findAll();

        assertThat(savedUsers)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(users.values());
    }

    @Test
    public void findUserById() {
        User userNumberOne = User.builder()
                .email("test@mail.ru")
                .login("test")
                .name("Тeст")
                .birthday(LocalDate.of(1993, Month.MAY, 8))
                .build();

        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        userNumberOne = userDbStorage.create(userNumberOne);

        User savedUser = userDbStorage.findUserById(userNumberOne.getId());

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(userNumberOne);
    }

    @Test
    public void addAFriend() {
        User userNumberOne = User.builder()
                .email("test@mail.ru")
                .login("test")
                .name("Тeст")
                .birthday(LocalDate.of(1993, Month.MAY, 8))
                .build();

        User userNumberTwo = User.builder()
                .email("test2@mail.ru")
                .login("test2")
                .name("Тeст2")
                .birthday(LocalDate.of(2018, Month.FEBRUARY, 8))
                .build();
        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        userNumberOne = userDbStorage.create(userNumberOne);
        userNumberTwo = userDbStorage.create(userNumberTwo);
        userDbStorage.addAFriend(userNumberTwo.getId(), userNumberOne.getId());

        List<User> friendsExpected = new ArrayList<>();
        friendsExpected.add(userNumberOne);

        List<User> friendsActual = userDbStorage.getFriendsList(userNumberTwo.getId());

        assertThat(friendsActual)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(friendsExpected);
    }

    @Test
    public void getFriendsList() {
        User userNumberOne = User.builder()
                .email("test@mail.ru")
                .login("test")
                .name("Тeст")
                .birthday(LocalDate.of(1993, Month.MAY, 8))
                .build();

        User userNumberTwo = User.builder()
                .email("test2@mail.ru")
                .login("test2")
                .name("Тeст2")
                .birthday(LocalDate.of(2018, Month.FEBRUARY, 8))
                .build();

        User userNumberThree = User.builder()
                .email("test3@mail.ru")
                .login("test3")
                .name("Тeст3")
                .birthday(LocalDate.of(2007, Month.APRIL, 15))
                .build();

        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        userNumberOne = userDbStorage.create(userNumberOne);
        userNumberTwo = userDbStorage.create(userNumberTwo);
        userNumberThree = userDbStorage.create(userNumberThree);
        userDbStorage.addAFriend(userNumberTwo.getId(), userNumberOne.getId());
        userDbStorage.addAFriend(userNumberTwo.getId(), userNumberThree.getId());

        List<User> friendsExpected = new ArrayList<>();
        friendsExpected.add(userNumberOne);
        friendsExpected.add(userNumberThree);

        List<User> friendsActual = userDbStorage.getFriendsList(userNumberTwo.getId());

        assertThat(friendsActual)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(friendsExpected);
    }

    @Test
    public void deleteFriend() {
        User userNumberOne = User.builder()
                .email("test@mail.ru")
                .login("test")
                .name("Тeст")
                .birthday(LocalDate.of(1993, Month.MAY, 8))
                .build();

        User userNumberTwo = User.builder()
                .email("test2@mail.ru")
                .login("test2")
                .name("Тeст2")
                .birthday(LocalDate.of(2018, Month.FEBRUARY, 8))
                .build();

        User userNumberThree = User.builder()
                .email("test3@mail.ru")
                .login("test3")
                .name("Тeст3")
                .birthday(LocalDate.of(2007, Month.APRIL, 15))
                .build();

        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        userNumberOne = userDbStorage.create(userNumberOne);
        userNumberTwo = userDbStorage.create(userNumberTwo);
        userNumberThree = userDbStorage.create(userNumberThree);
        userDbStorage.addAFriend(userNumberTwo.getId(), userNumberOne.getId());
        userDbStorage.addAFriend(userNumberTwo.getId(), userNumberThree.getId());
        userDbStorage.deleteFriend(userNumberTwo.getId(), userNumberThree.getId());

        List<User> friendsExpected = new ArrayList<>();
        friendsExpected.add(userNumberOne);
        friendsExpected.add(userNumberThree);
        friendsExpected.remove(userNumberThree);

        List<User> friendsActual = userDbStorage.getFriendsList(userNumberTwo.getId());
        System.out.println(userDbStorage.getFriendsList(userNumberTwo.getId()));

        assertThat(friendsActual)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(friendsExpected);
    }

    @Test
    public void displayAListOfCommonFriends() {
        User userNumberOne = User.builder()
                .email("test@mail.ru")
                .login("test")
                .name("Тeст")
                .birthday(LocalDate.of(1993, Month.MAY, 8))
                .build();

        User userNumberTwo = User.builder()
                .email("test2@mail.ru")
                .login("test2")
                .name("Тeст2")
                .birthday(LocalDate.of(2018, Month.FEBRUARY, 8))
                .build();

        User userNumberThree = User.builder()
                .email("test3@mail.ru")
                .login("test3")
                .name("Тeст3")
                .birthday(LocalDate.of(2007, Month.APRIL, 15))
                .build();

        User userNumberFour = User.builder()
                .email("test4@mail.ru")
                .login("test4")
                .name("Тeст4")
                .birthday(LocalDate.of(2007, Month.SEPTEMBER, 22))
                .build();

        UserDbStorage userDbStorage = new UserDbStorage(jdbcTemplate);
        userNumberOne = userDbStorage.create(userNumberOne);
        userNumberTwo = userDbStorage.create(userNumberTwo);
        userNumberThree = userDbStorage.create(userNumberThree);
        userNumberFour = userDbStorage.create(userNumberFour);
        userDbStorage.addAFriend(userNumberOne.getId(), userNumberThree.getId());
        userDbStorage.addAFriend(userNumberTwo.getId(), userNumberThree.getId());
        userDbStorage.addAFriend(userNumberOne.getId(), userNumberFour.getId());
        userDbStorage.addAFriend(userNumberTwo.getId(), userNumberFour.getId());

        List<User> friendsExpected = new ArrayList<>();
        friendsExpected.add(userNumberThree);
        friendsExpected.add(userNumberFour);

        List<User> friendsActual = userDbStorage.displayAListOfCommonFriends(userNumberOne.getId(), userNumberTwo.getId());

        assertThat(friendsActual)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(friendsExpected);
    }

}