package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DBGenreStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreMapper mapper;
    private static final String FIND_BY_ID = "SELECT * FROM genres WHERE ID = ?";
    private static final String FIND_ALL = "SELECT * FROM genres";
    private static final String FIND_BY_FILM = "SELECT * FROM genres g " +
            "INNER JOIN film_genres fg ON fg.GENRE_ID = g.ID " +
            "WHERE fg.FILM_ID = ?" +
            "ORDER BY g.ID";
    private static final String FIND_GENRES_BY_IDS = "SELECT * FROM genres WHERE ID IN (%s)";

    @Override
    public Genre getGenreById(Long genreId) {
        List<Genre> result = jdbcTemplate.query(FIND_BY_ID, mapper, genreId);
        if (result.isEmpty()) {
            throw new NotFoundException("Genre with id " + genreId + " not found");
        }
        return result.getFirst();
    }

    @Override
    public ArrayList<Genre> getGenres(Film film) {
        List<String> ids = new ArrayList<>();

        for (Genre genre : film.getGenres()) {
            ids.add(genre.getId().toString());
        }

        String in = String.join(",", Collections.nCopies(ids.size(), "?"));
        return (ArrayList<Genre>) jdbcTemplate.query(
                String.format(FIND_GENRES_BY_IDS, in),
                mapper,
                ids.toArray()
        );
    }

    @Override
    public List<Genre> getGenresByFilm(Film film) {
        return jdbcTemplate.query(FIND_BY_FILM, mapper, film.getId());
    }

    @Override
    public List<Genre> getAll() {
        List<Genre> result = jdbcTemplate.query(FIND_ALL, mapper);
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }
}