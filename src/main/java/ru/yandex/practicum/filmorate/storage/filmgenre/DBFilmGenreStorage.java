package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.filmgenre.FilmGenreStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component("dbFilmGenreStorage")
@RequiredArgsConstructor
public class DBFilmGenreStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_QUERY = "INSERT INTO film_genres (FILM_ID, GENRE_ID) VALUES (?, ?)";

    private static final String DELETE_QUERY = "DELETE from film_genres WHERE FILM_ID = ?";

    @Override
    public void addFilmGenres(Film film) {
        jdbcTemplate.update(DELETE_QUERY, film.getId());

        jdbcTemplate.batchUpdate(
                INSERT_QUERY,
                new AddGenresPreparedStatementSetter(film.getGenres().stream().toList(), film.getId())
        );
    }

    static class AddGenresPreparedStatementSetter implements BatchPreparedStatementSetter {
        private final List<Genre> genreSet;
        private final Integer filmId;

        AddGenresPreparedStatementSetter(List<Genre> genreSet, Integer filmId) {
            this.genreSet = genreSet;
            this.filmId = filmId;
        }

        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
            Genre genre = genreSet.get(i);
            ps.setLong(1, filmId);
            ps.setLong(2, genre.getId());
        }

        @Override
        public int getBatchSize() {
            return genreSet.size();
        }
    }
}