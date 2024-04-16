package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private Integer id;
    @Email
    private String email;
    @NotNull
    @NotBlank
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
    private final Set<Integer> friends = new LinkedHashSet<Integer>();
    private final Set<Integer> films = new LinkedHashSet<Integer>();
}