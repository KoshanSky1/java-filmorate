package ru.yandex.practicum.filmorate.model.feed;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Event {
    private int eventId;
    private Long timestamp;
    private Integer userId;
    private EventType eventType;
    private Operation operation;
    private Integer entityId;
}
