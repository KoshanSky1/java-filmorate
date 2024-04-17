package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Data
public class Mpa {
    private int id;
    @NotNull(message = "Name is required")
    private String name;
}
