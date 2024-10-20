package ru.yandex.practicum.filmorate.service.film;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmgenre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage fs;
    private final GenreStorage gs;
    private final MpaStorage ms;
    private final FilmGenreStorage fgs;

    public Film addFilm(Film film) {
        MPA mpa = ms.getMpaById(film.getMpa().getId());
        if (mpa == null) {
            throw new ValidationException("MPA с id = " + film.getMpa().getId() + " не найден");
        }

        List<Genre> genres;
        if (film.getGenres() != null) {
            genres = gs.getGenres(film);

            if (genres.size() != film.getGenres().size()) {
                throw new ValidationException("Жанр не найден");
            }
        } else {
            film.setGenres(new HashSet<>());
            genres = new ArrayList<>();
        }
        film = fs.addFilm(film);
        fgs.addFilmGenres(film);

        film.setMpa(mpa);
        film.setGenres(new LinkedHashSet<>(genres));
        return film;
    }

    public Film updateFilm(Film film) {
        MPA mpa = ms.getMpaById(film.getMpa().getId());
        if (mpa == null) {
            throw new ValidationException("MPA с id = " + film.getMpa().getId() + " не найден");
        }

        List<Genre> genres;
        if (film.getGenres() != null) {
            genres = gs.getGenres(film);

            if (genres.size() != film.getGenres().size()) {
                throw new ValidationException("Жанр не найден");
            }
        } else {
            film.setGenres(new HashSet<>());
            genres = new ArrayList<>();
        }
        film = fs.updateFilm(film);
        fgs.addFilmGenres(film);
        film.setMpa(mpa);
        film.setGenres(new HashSet<>(genres));

        return film;
    }

    public Collection<Film> getAll() {
        return fs.getAll();
    }

    public Film getFilm(int id) {
        Film film = fs.getFilm(id);

        if (film == null) {
            return null;
        }

        List<Genre> genres = gs.getGenresByFilm(film);
        film.setGenres(new HashSet<>(genres));

        return film;
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
