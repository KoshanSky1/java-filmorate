package ru.yandex.practicum.filmorate.exeption;

public class ReviewCreateException extends RuntimeException {
    public ReviewCreateException(String message) {
        super(message);
    }
}
