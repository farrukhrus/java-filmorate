package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface FilmStorage {
    public Film addFilm(Film film);

    public Film updateFilm(Film film);

    public Film getFilm(int id);

    public Collection<Film> getAll();

    public Collection<Film> getPopular(int count);

    public Film addLike(int id, int userId);

    public Film removeLike(int id, int userId);
}
