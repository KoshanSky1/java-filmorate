package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Friendship {
    private User userFrom;
    private User userTo;
    private boolean status;
}