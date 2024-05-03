package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.feed.Event;
import ru.yandex.practicum.filmorate.model.feed.EventType;
import ru.yandex.practicum.filmorate.model.feed.Operation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository("FeedDbStorage")
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getFeed(int idUser) {
        String sql =
                "select * " +
                        "from F04_FEED where U01_ID = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEvent(rs), idUser);
    }

    @Override
    public Event addEvent(Event event) {
        String sql =
                "insert into F04_FEED " +
                        "(F04_TIMESTAMP, U01_ID, F04_EVENT_TYPE, F04_OPERATION, F04_ENTITY_ID) " +
                        "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setLong(1, event.getTimestamp());
            stmt.setInt(2, event.getUserId());
            stmt.setString(3, event.getEventType().name());
            stmt.setString(4, event.getOperation().name());
            stmt.setInt(5, event.getEntityId());
            return stmt;
        }, keyHolder);
        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        event.setEventId(id);
        return event;
    }

    private Event makeEvent(ResultSet resultSet) throws SQLException {
        return Event.builder()
                .eventId(resultSet.getInt("F04_ID"))
                .userId(resultSet.getInt("U01_ID"))
                .timestamp(resultSet.getLong("F04_TIMESTAMP"))
                .eventType(EventType.valueOf(resultSet.getString("F04_EVENT_TYPE")))
                .operation(Operation.valueOf(resultSet.getString("F04_OPERATION")))
                .entityId(resultSet.getInt("F04_ENTITY_ID"))
                .build();
    }
}
