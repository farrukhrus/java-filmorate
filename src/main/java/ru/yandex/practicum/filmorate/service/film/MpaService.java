package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public MPA getMpaById(Long id) {
        MPA mpa = mpaStorage.getMpaById(id);

        if (mpa == null) {
            throw new NotFoundException("MPA with id " + id + " not found");
        }

        return mpa;
    }

    public List<MPA> getAll() {
        return mpaStorage.getAll();
    }
}