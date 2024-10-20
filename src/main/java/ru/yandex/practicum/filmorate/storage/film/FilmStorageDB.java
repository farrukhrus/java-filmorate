package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Primary
@Repository
@RequiredArgsConstructor
public class FilmStorageDB implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final String FIND_BY_ID = "SELECT f.*, m.name AS mpa_name FROM films f " +
            "LEFT JOIN mpa m ON m.id = f.mpa_id WHERE f.id = ?";
    private static final String FIND_ALL = "SELECT f.*, m.name as mpa_name " +
            "FROM films f LEFT JOIN mpa m ON f.mpa_id = m.id";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, mpa_id = ? where id = ?";
    private static final String GET_POPULAR = "SELECT f.*, m.name AS mpa_name FROM films f JOIN ( " +
            "SELECT film_id, COUNT(user_id) AS rn " +
            "FROM likes l GROUP BY film_id " +
            "ORDER BY rn DESC LIMIT ?" +
            ") as subfilms ON f.ID = subfilms.film_id LEFT JOIN mpa m ON f.mpa_id = m.id ORDER BY subfilms.rn DESC";


    @Override
    public List<Film> getAll() {
        return jdbcTemplate.query(FIND_ALL, FilmStorageDB::mapper);
    }

    @Override
    public Collection<Film> getPopular(int count) {
        List<Film> films = jdbcTemplate.query(GET_POPULAR, FilmStorageDB::mapper, count);
        if (films.isEmpty()) {
            return List.of();
        }
        return films;
    }

    @Override
    public Film addLike(int id, int userId) {
        return null;
    }

    @Override
    public Film removeLike(int id, int userId) {
        return null;
    }

    @Override
    public Film addFilm(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            String errMessage = "Дата выпуска фильма должна быть не раньше 28 декабря 1895 года";
            throw new ConditionsNotMetException(errMessage);
        }

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, film.getName());
            ps.setObject(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setObject(4, film.getDuration());
            ps.setObject(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        Integer id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        film.setId(id);
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        if (newFilm.getReleaseDate() == null || newFilm.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            String errMessage = "Дата выпуска фильма должна быть не раньше 28 декабря 1895 года";
            throw new ConditionsNotMetException(errMessage);
        }

        jdbcTemplate.update(UPDATE_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getMpa().getId(),
                newFilm.getId()
        );
        return newFilm;
    }

    @Override
    public Film getFilm(Integer id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID, FilmStorageDB::mapper, id);
    }

    public static Film mapper(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new MPA(rs.getLong("mpa_id"), rs.getString("mpa_name")))
                .build();
    }
}