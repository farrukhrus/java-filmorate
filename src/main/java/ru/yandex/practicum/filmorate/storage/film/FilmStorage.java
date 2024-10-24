package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilm(Integer id);

    Collection<Film> getAll();

    Collection<Film> getPopular(int count);

    void addLike(int id, int userId);

    void removeLike(int id, int userId);
}
