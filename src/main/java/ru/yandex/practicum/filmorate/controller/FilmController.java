package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final int DESC_MAX_LENGTH = 200;
    private static final LocalDate EARLIEST_RELEASE_DATE =
            LocalDate.of(1895, Month.DECEMBER, 28);
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Добавление фильма {}", film.getName());
        log.trace(film.toString());
        String errMessage;

        if (film.getName().isBlank()) {
            errMessage = "Название фильма не должно быть пустым";
            log.error(errMessage);
            throw new ConditionsNotMetException(errMessage);
        }
        if (film.getDescription().length() > DESC_MAX_LENGTH) {
            errMessage = "Описание не должно превышать 200 символов";
            log.error(errMessage);
            throw new ConditionsNotMetException(errMessage);
        }
        if (film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            errMessage = "Дата выпуска фильма должна быть не раньше 28 декабря 1895 года";
            log.error(errMessage);
            throw new ConditionsNotMetException(errMessage);
        }
        if (film.getDuration() <= 0) {
            errMessage = "Продолжительность фильма должна быть положительным числом";
            log.error(errMessage);
            throw new ConditionsNotMetException(errMessage);
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм {} успешно создан с ID={}", film.getName(), film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Обновление фильма с ID={}", newFilm.getId());
        log.trace(newFilm.toString());
        String errMessage;

        if (newFilm.getId() == 0) {
            errMessage = "Id должен быть указан";
            log.error(errMessage);
            throw new ConditionsNotMetException(errMessage);
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (!(newFilm.getDescription() == null || newFilm.getDescription().isBlank())) {
                oldFilm.setDescription((newFilm.getDescription()));
            }
            if (!(newFilm.getName() == null || newFilm.getName().isBlank())) {
                oldFilm.setName(newFilm.getName());
            }
            if (newFilm.getReleaseDate() != null) {
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            if (newFilm.getDuration() < 200) {
                oldFilm.setDuration(newFilm.getDuration());
            }
            log.info("Фильм с ID={} успешно обновлен", newFilm.getId());
            return oldFilm;
        }
        errMessage = "Фильм с id = " + newFilm.getId() + " не найден";
        log.error(errMessage);
        throw new NotFoundException(errMessage);
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