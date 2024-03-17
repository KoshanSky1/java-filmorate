package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.constraint.NameConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NameConstraint
public class User {

    private int id;
    @Email(message = "Email is incorrect")
    @NotNull(message = "Email is required")
    private String email;
    @NotNull(message = "Login is required")
    @Pattern(regexp = "\\S+", message = "Login must not contain space characters")
    private String login;
    private String name;
    @NotNull(message = "Birthday is required")
    @PastOrPresent(message = "Birthday must not be later than the current date")
    private LocalDate birthday;
    private final Set<Integer> friends = new HashSet<>();

    public void addFriend(final Integer id) {
        friends.add(id);
    }

    public void deleteFriend(final Integer id) {
        friends.remove(id);
    }
}
