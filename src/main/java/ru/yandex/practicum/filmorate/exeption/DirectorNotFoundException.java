package ru.yandex.practicum.filmorate.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public class DirectorNotFoundException extends RuntimeException {

        public DirectorNotFoundException(final String message) {
            super(message);
        }

    }