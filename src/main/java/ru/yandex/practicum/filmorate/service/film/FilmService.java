package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage fs;

    public Film addFilm(Film film) {
        return fs.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return fs.updateFilm(film);
    }

    public Collection<Film> getAll() {
        return fs.getAll();
    }

    public Collection<Film> getPopular(int count) {
        return fs.getPopular(count);
    }

    public Film addLike(int id, int userId) {
        return fs.addLike(id, userId);
    }

    public Film removeLike(int id, int userId) {
        return fs.removeLike(id, userId);
    }
}
