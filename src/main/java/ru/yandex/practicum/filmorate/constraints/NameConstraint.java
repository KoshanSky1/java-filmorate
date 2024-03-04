package ru.yandex.practicum.filmorate.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = {NameConstraintValidator.class})
public @interface NameConstraint {


    /**
     * Provide validation failed message. Default resolves to configurable property.
     *
     * @return validation failed message
     */
    String message() default "{Cannot check value by custom constraint}";

    /**
     * Configure constraint groups that can be selected together.
     *
     * @return configured groups
     */
    Class<?>[] groups() default {};

    /**
     * Allow extensibility of the constraint.
     *
     * @return payload object
     */
    Class<? extends Payload>[] payload() default {};
}
