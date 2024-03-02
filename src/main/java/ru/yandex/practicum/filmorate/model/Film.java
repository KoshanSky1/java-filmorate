package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Film.
 */
@Data
public class Film {

    @NotNull(message = "Id is required")
    private int id;
    @NotNull(message = "Name is required")
    private String name;
    @NotBlank(message = "Description is required")
    @Size(max = 200, message = "Description must be maximum 200 characters")
    private String description;
    @NotNull(message = "Release date is required")
    private Date releaseDate;
    @Positive(message = "Duration is required and must be greater than 0")
    private long duration;
}
