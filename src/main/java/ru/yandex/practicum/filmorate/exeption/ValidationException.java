package ru.yandex.practicum.filmorate.exeption;

public class ValidationException extends RuntimeException {
    private final long code;

    public ValidationException(String message, long code) {
        super(message);
        this.code = code;
    }
}
