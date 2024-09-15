package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    private final Map<Integer, Film> films = new HashMap<>();
    private final UserStorage us;

    @Override
    public Film addFilm(Film film) {
        log.info("Добавление фильма {}", film.getName());
        log.trace(film.toString());
        String errMessage;
        if (film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            errMessage = "Дата выпуска фильма должна быть не раньше 28 декабря 1895 года";
            log.error(errMessage);
            throw new ConditionsNotMetException(errMessage);
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм {} успешно создан с ID={}", film.getName(), film.getId());
        return film;
    }

    @Override
    public Film getFilm(int id) {
        return null;
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film updateFilm(Film film) {
        log.info("Обновление фильма с ID={}", film.getId());
        log.trace(film.toString());
        String errMessage;

        if (film.getId() == 0) {
            errMessage = "Id должен быть указан";
            log.error(errMessage);
            throw new ConditionsNotMetException(errMessage);
        }
        if (films.containsKey(film.getId())) {
            Film oldFilm = films.get(film.getId());
            if (film.getReleaseDate().isAfter(EARLIEST_RELEASE_DATE)) {
                oldFilm.setReleaseDate(film.getReleaseDate());
            } else {
                errMessage = "Дата выпуска фильма должна быть не раньше 28 декабря 1895 года";
                log.error(errMessage);
                throw new ConditionsNotMetException(errMessage);
            }
            if (film.getDescription() != null && !film.getDescription().isBlank()) {
                oldFilm.setDescription((film.getDescription()));
            }
            if (film.getDescription() != null) {
                oldFilm.setDuration(film.getDuration());
            }
            if (film.getName() != null) {
                oldFilm.setName(film.getName());
            }

            log.info("Фильм с ID={} успешно обновлен", film.getId());
            return oldFilm;
        }
        errMessage = "Фильм с id = " + film.getId() + " не найден";
        log.error(errMessage);
        throw new NotFoundException(errMessage);
    }

    @Override
    public Collection<Film> getPopular(int count) {
        log.info("Получение списка самых популярных постов");

        Comparator<Film> comparator = Comparator.comparing(Film::getLikes);
        return films.values().stream()
                .sorted(comparator.reversed())
                .limit(count)
                .toList();
    }

    @Override
    public Film addLike(int id, int userId) {
        log.info("Польвателю с ID = {} понравился фильм с ID = {}", id, userId);
        String errMessage;

        if (!checkFilm(id)) {
            errMessage = "Фильм с id = " + id + " не найден";
            log.error(errMessage);
            throw new NotFoundException(errMessage);
        }
        if (!us.checkUser(userId)) {
            errMessage = "Пользователь с id = " + userId + " не найден";
            log.error(errMessage);
            throw new NotFoundException(errMessage);
        }
        Film film = films.get(id);
        film.addLike(userId);
        return film;
    }

    @Override
    public Film removeLike(int id, int userId) {
        log.info("Польватель с ID = {} удалил лайк у фильма с ID = {}", id, userId);
        String errMessage;
        if (!checkFilm(id)) {
            errMessage = "Фильм с id = " + id + " не найден";
            log.error(errMessage);
            throw new NotFoundException(errMessage);
        }
        if (!us.checkUser(userId)) {
            errMessage = "Пользователь с id = " + userId + " не найден";
            log.error(errMessage);
            throw new NotFoundException(errMessage);
        }
        Film film = films.get(id);
        film.removeLike(userId);
        return film;
    }

    public boolean checkFilm(int id) {
        return films.containsKey(id);
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        log.trace("Очередной ID фильма: {}", currentMaxId);
        return ++currentMaxId;
    }
}
