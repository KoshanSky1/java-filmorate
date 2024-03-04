package ru.yandex.practicum.filmorate.constraints;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class NameConstraintValidator implements ConstraintValidator<NameConstraint, User> {

    @Override
    public void initialize(NameConstraint constraintAnnotation) {
        // Perform any initialization of the instance of the constraint annotation
        // For example, assert that extra parameters are valid
    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {

        // check what login is not null or empty
        String login = user.getLogin();

        if (login.isBlank() || login.isEmpty()) {
            log.warn("Warning! The login of User object is null or empty. Cannot apply NameConstraint to User.name");
            return false;
        }

        // if value is null, check login is null and set default login
        if (user.getName() == null || user.getName().isEmpty()) {

            //set default login name
            user.setName(login);
        }

        return true;
    }


}
