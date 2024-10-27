package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MPA;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final MpaMapper mapper;
    private final JdbcTemplate jdbcTemplate;
    private static final String FIND_MPA_BY_ID = "SELECT * FROM mpa WHERE ID = ?";
    private static final String FIND_ALL = "SELECT * FROM mpa";

    @Override
    public MPA getMpaById(Long id) {
        List<MPA> result = jdbcTemplate.query(FIND_MPA_BY_ID, mapper, id);
        if (result.isEmpty()) {
            return null;
        }
        return result.getFirst();
    }

    @Override
    public List<MPA> getAll() {
        return jdbcTemplate.query(FIND_ALL, mapper);
    }
}