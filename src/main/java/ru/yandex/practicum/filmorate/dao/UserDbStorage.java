package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;


@Slf4j
@Component("UserDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User getUser(int idUser) {
        String sqlUser =
                "select * " +
                        "from U01_USER " +
                        "where U01_ID = ?";
        User user;
        try {
            user = jdbcTemplate.queryForObject(sqlUser, (rs, rowNum) -> makeUser(rs), idUser);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(format("User [%s] not found in DB", idUser));
        }

        return user;
    }

    @Override
    public List<User> getAllUsers() {
        String sqlAllUsers =
                "select * " +
                        "from U01_USER";

        return jdbcTemplate.query(sqlAllUsers, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User createUser(User user) {
        String sqlQuery =
                "insert into U01_USER " +
                        "(U01_EMAIL, U01_LOGIN, U01_NAME, U01_BIRTHDAY) " +
                        "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));

            return preparedStatement;
        }, keyHolder);

        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();

        log.info(format("User [%s] was create", id));
        return getUser(id);
    }

    @Override
    public User updateUser(User user) {
        try {
            String sqlUser =
                    "update U01_USER set " +
                            "U01_EMAIL = ?, U01_LOGIN = ?, U01_NAME = ?, U01_BIRTHDAY = ? " +
                            "where U01_ID = ?";
            jdbcTemplate.update(sqlUser,
                    user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(format("User [%s] not found in DB", user.getId()));
        }

        log.info(format("User [%s] was create", user.getId()));
        return getUser(user.getId());
    }

    @Override
    public boolean deleteUser(int idUser) {
        String sqlQuery =
                "delete from U01_USER " +
                        "where U01_ID = ?";

        log.info(format("User [%s] was delete", idUser));
        return jdbcTemplate.update(sqlQuery, idUser) > 0;
    }

    @Override
    public void deleteFriend(int idUser, int idFriend) {
        String sql =
                "delete from F03_FRIENDS " +
                        "where U01_ID = ? and U01_ID_FRIEND = ?";

        log.info(format("Friendship: User [%s] and friend [%s] was delete", idUser, idFriend));
        jdbcTemplate.update(sql, idUser, idFriend);
    }

    @Override
    public List<User> getFriends(int idUser) {
        String sql =
                "select u.* " +
                        "from F03_FRIENDS AS f " +
                        "join U01_USER AS u " +
                        "on f.U01_ID_FRIEND = u.U01_ID " +
                        "where f.U01_ID =?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), idUser);
    }

    @Override
    public List<User> getCommonFriends(int idUser, int idFriend) {
        String sql =
                "with u1 as (" +
                        "select u.* " +
                        "from F03_FRIENDS as f " +
                        "join U01_USER as u " +
                        "on f.U01_ID_FRIEND = u.U01_ID " +
                        "where f.U01_ID = ? " +
                        "), " +
                        "u2 as ( " +
                        "select u.* " +
                        "from F03_FRIENDS as f " +
                        "join U01_USER as u " +
                        "on f.U01_ID_FRIEND = u.U01_ID " +
                        "where f.U01_ID = ? " +
                        ") " +
                        "select u1.* " +
                        "from u1 " +
                        "join u2 " +
                        "on u1.U01_ID = u2.U01_ID ";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), idUser, idFriend);
    }

    @Override
    public void addFriend(int idUser, int idFriend) {

        String sql =
                "INSERT INTO F03_FRIENDS (U01_ID, U01_ID_FRIEND, S01_ID) " +
                        "VALUES (?, ?, 1)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, idUser);
            preparedStatement.setInt(2, idFriend);

            return preparedStatement;
        }, keyHolder);

        checkConfirmation(idUser, idFriend);
    }

    private User makeUser(ResultSet resultSet) throws SQLException {
        int userId = resultSet.getInt("U01_ID");
        return new User(
                userId,
                resultSet.getString("U01_EMAIL"),
                resultSet.getString("U01_LOGIN"),
                resultSet.getString("U01_NAME"),
                Objects.requireNonNull(resultSet.getDate("U01_BIRTHDAY")).toLocalDate());
    }

    private void checkConfirmation(int idUser, int idFriend) {
        String sql =
                "update F03_FRIENDS as f1 " +
                        "set S01_ID = 2 " +
                        "where U01_ID = ? and U01_ID in ( " +
                        "select f2.U01_ID_FRIEND " +
                        "from F03_FRIENDS as f2 " +
                        "where f2.U01_ID = ? and f2.U01_ID = f1.U01_ID_FRIEND) ";

        jdbcTemplate.update(sql, idUser, idFriend);

        sql =
                "update F03_FRIENDS as f1 " +
                        "set S01_ID = 2 " +
                        "where U01_ID = ? and U01_ID not in ( " +
                        "select f2.U01_ID_FRIEND " +
                        "from F03_FRIENDS as f2 " +
                        "where f2.U01_ID = ? and f2.U01_ID = f1.U01_ID_FRIEND) ";

        jdbcTemplate.update(sql, idUser, idFriend);
    }

}
