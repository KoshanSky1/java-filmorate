package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;

    @Override
    public void deleteFilmGenre(long idFilm) {
        String sql =
                "delete " +
                        "from F02_FILM_GENRE " +
                        "where F01_ID = ? ";

        jdbcTemplate.update(sql, idFilm);
    }

    @Override
    public List<Genre> createFilmGenre(int idFilm, List<Genre> genres) {
        String sql =
                "insert into F02_FILM_GENRE " +
                        "(G01_ID, F01_ID) " +
                        "values (?, ?)";

        if (genres == null || genres.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Genre> genreSort = new TreeSet<>(Comparator.comparingDouble(Genre::getId));
        genreSort.addAll(genres);

        for (Genre genre : genreSort) {
            try {
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcTemplate.update(connection -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setInt(1, genre.getId());
                    preparedStatement.setInt(2, idFilm);

                    return preparedStatement;
                }, keyHolder);
            } catch (Exception ex) {
                throw new ValidationException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }

        return genreDbStorage.getGenresListForFilm(idFilm);
    }

}
