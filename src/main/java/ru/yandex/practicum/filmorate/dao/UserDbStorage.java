package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
@Repository
//@Component
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findAll() {
        String sqlQuery = "SELECT * from USERS";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User create(User user) {
        log.info("Создан новый пользователь: " + findUserById(saveAndReturnId(user)));
        return findUserById(saveAndReturnId(user));
    }

    @Override
    public User put(User user) {
       findUserById(user.getId());
            String sqlQuery = "UPDATE users SET " +
                    "user_email = ?, user_login = ?, user_name = ?, user_birthday = ?" +
                    "WHERE user_id = ?";
            jdbcTemplate.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
            log.info("Пользователь (id = " + user.getId() + ") успешно обновлён");
            return user;
    }

    @Override
    public void delete(User user) {
        String sqlQuery = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sqlQuery, user.getId());
        log.info("Пользователь (id = " + user.getId() + ") успешно удалён");
    }

    @Override
    public User findUserById(Integer userId) {
        String sqlQuery = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        if(userRows.next()) {
            log.info("Найден пользователь: {} {}", userRows.getString("user_id"), userRows.getString("user_login"));
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, userId);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", userId);
            throw new UserNotFoundException("Пользователь с идентификатором " + userId + "не найден.");
        }
    }

    @Override
    public void addAFriend(Integer id, Integer friendId) {
        findUserById(id);
        findUserById(friendId);
        boolean status = checkFriendShipStatus(friendId,id);
        String sqlQuery = "insert into friendship(user_from_id, user_to_id, status) " +
                "values (?, ?, ?)";
        if(status) {
                jdbcTemplate.update(sqlQuery, friendId, id, true);
        } else {
                jdbcTemplate.update(sqlQuery, id, friendId, false);
        }
        log.info("Cоздана дружба пользователей: " + id + " и " + friendId + "статус: " + status);
    }

    @Override
    public List<User> getFriendsList(Integer id) {
        findUserById(id);

        List<User> friends = new ArrayList<>();
        String sqlQuery = "SELECT * FROM users AS u " +
                "WHERE u.user_id IN (SELECT user_to_id FROM friendship " + "WHERE user_from_id = ?);";
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        while (friendsRows.next()) {
            friends.add(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id));
        }
        return friends;
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
        findUserById(id);
        findUserById(friendId);
        String sqlUserFrom = "delete from friendship where user_from_id = ? and user_to_id = ?";
        jdbcTemplate.update(sqlUserFrom, id, friendId);
        log.info("Дружба между пользователями " + id + " и " + friendId + " успешно удалена");
    }

    @Override
    public List<User> displayAListOfCommonFriends(Integer id, Integer otherId) {
        List<User> commonFriends = new ArrayList<>();
        String sqlQuery = "SELECT * FROM users " +
                "WHERE user_id IN (SELECT user_to_id FROM friendship " +
                "WHERE user_from_id IN(?, ?) " +
                "AND user_to_id NOT IN (?, ?));";
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(sqlQuery, id, otherId, id, otherId);
        while (friendsRows.next()) {
            commonFriends.add(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id, otherId, id, otherId));
        }
        log.info("Сформирован список общих друзей для пользователей: " + id + " и " + otherId);
        return commonFriends;
    }

    @Override
    public Map<Integer, User> getUsers() {
        return null;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("USER_ID"))
                .email(resultSet.getString("USER_EMAIL"))
                .login(resultSet.getString("USER_LOGIN"))
                .name(resultSet.getString("USER_NAME"))
                .birthday(resultSet.getDate("USER_BIRTHDAY").toLocalDate())
                .build();
    }

    private Integer saveAndReturnId(User user) {
        String sqlQuery = "INSERT INTO users(user_email, user_login, user_name, user_birthday) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    private boolean checkFriendShipStatus(Integer id, Integer friendId) {
        String sqlQuery = "SELECT * FROM friendship WHERE user_from_id = ? "
                + "AND user_to_id = ?;";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id, friendId);
        return userRows.next();
    }

    private Friendship mapRowToFriendship(ResultSet resultSet, int rowNum) throws SQLException {
        return Friendship.builder()
                .userFrom(findUserById(resultSet.getInt("USER_FROM_ID")))
                .userTo(findUserById(resultSet.getInt("USER_TO_ID")))
                .status(resultSet.getBoolean("STATUS"))
                .build();
    }

}