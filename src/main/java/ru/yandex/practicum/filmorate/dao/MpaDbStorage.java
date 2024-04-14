package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

@Component("MpaDbStorage")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;


    @Override
    public Mpa getMpa(int idMpa) {
        String sqlMpa = "select * from M01_MPA where M01_ID = ?";
        Mpa mpa;
        try {
            mpa = jdbcTemplate.queryForObject(sqlMpa, (rs, rowNum) -> makeMpa(rs), idMpa);
        } catch (EmptyResultDataAccessException e) {
            throw new MpaNotFoundException(format("Mpa [%s] not found in DB", idMpa));
        }
        return mpa;
    }

    @Override
    public List<Mpa> getAllMpas() {
        String sqlAllMpas = "select * from M01_MPA";
        return jdbcTemplate.query(sqlAllMpas, (rs, rowNum) -> makeMpa(rs));
    }


    @Override
    public Mpa createMpa(Mpa mpa) {
        String sqlQuery = "insert into M01_MPA " +
                "(M01_NAME) " +
                "values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, mpa.getName());

            return preparedStatement;
        }, keyHolder);

        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();

        return getMpa(id);
    }

    @Override
    public Mpa updateMpa(Mpa mpa) {
        try {
            String sqlMpa = "update M01_MPA set " +
                    "M01_NAME = ? " +
                    "where M01_ID = ?";
            jdbcTemplate.update(sqlMpa,
                    mpa.getName(), mpa.getId());
        } catch (EmptyResultDataAccessException e) {
            throw new MpaNotFoundException(format("Mpa [%s] not found in DB", mpa.getId()));
        }
        return getMpa(mpa.getId());
    }


    @Override
    public boolean deleteMpa(int idMpa) {
        String sqlQuery = "delete from M01_MPA where M01_ID = ?";
        return jdbcTemplate.update(sqlQuery, idMpa) > 0;
    }

    private Mpa makeMpa(ResultSet resultSet) throws SQLException {
        int idMpa = resultSet.getInt("M01_ID");
        return new Mpa(
                idMpa,
                resultSet.getString("M01_NAME"));
    }
}
