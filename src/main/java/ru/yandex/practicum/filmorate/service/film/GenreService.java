package ru.yandex.practicum.filmorate.service.film;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.db.DBGenreStorage;

import java.util.List;

@Service
public class GenreService {
    private final GenreStorage genreStorage;

    public GenreService(DBGenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getById(Long id) {
        return genreStorage.getGenreById(id);
    }

    public List<Genre> getList() {
        return genreStorage.getAll();
    }
}