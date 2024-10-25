package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Primary
@Repository
@Slf4j
@RequiredArgsConstructor
public class FilmStorageDB implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper mapper;
    private String errMessage;

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
    private static final String ADD_LIKE = "INSERT INTO likes (FILM_ID, USER_ID) VALUES (?, ?)";
    private static final String DELETE_LIKE = "DELETE from likes WHERE FILM_ID = ? AND USER_ID = ?";

    @Override
    public List<Film> getAll() {
        log.info("Получение всего списка фильмов");
        return jdbcTemplate.query(FIND_ALL, mapper);
    }

    @Override
    public Collection<Film> getPopular(int count) {
        log.info("Получение списка самых популярных постов");
        List<Film> films = jdbcTemplate.query(GET_POPULAR, mapper, count);
        if (films.isEmpty()) {
            return List.of();
        }
        return films;
    }

    @Override
    public void addLike(int id, int userId) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(ADD_LIKE);
            ps.setObject(1, id);
            ps.setObject(2, userId);
            return ps;
        });
    }

    @Override
    public void removeLike(int id, int userId) {
        jdbcTemplate.update(DELETE_LIKE, id, userId);
    }

    @Override
    public Film addFilm(Film film) {
        log.info("Добавление фильма {}", film.getName());
        log.trace(film.toString());
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            errMessage = "Дата выпуска фильма должна быть не раньше 28 декабря 1895 года";
            log.error(errMessage);
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
        log.info("Фильм {} успешно создан с ID={}", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        log.info("Обновление фильма с ID={}", newFilm.getId());
        log.trace(newFilm.toString());

        if (getFilm(newFilm.getId()) == null) {
            errMessage = "Фильм не найден " + newFilm.getId();
            log.info(errMessage);
            throw new NotFoundException(errMessage);
        }

        if (newFilm.getReleaseDate() == null || newFilm.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            errMessage = "Дата выпуска фильма должна быть не раньше 28 декабря 1895 года";
            log.error(errMessage);
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
        log.info("Фильм с ID={} успешно обновлен", newFilm.getId());
        return newFilm;
    }

    @Override
    public Film getFilm(Integer id) {
        log.info("Получение фильма по ID {}", id);
        List<Film> film = jdbcTemplate.query(FIND_BY_ID, mapper, id);
        if (film.isEmpty()) {
            return null;
        }
        return film.getFirst();
    }
}