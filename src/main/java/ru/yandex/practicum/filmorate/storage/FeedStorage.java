package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.feed.Event;

import java.util.List;

public interface FeedStorage {
    List<Event> getFeed(int idUser);

    Event addEvent(Event event);
}
