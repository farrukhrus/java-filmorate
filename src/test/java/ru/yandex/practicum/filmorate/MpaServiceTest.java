package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.film.MpaService;
import java.util.Collection;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MpaServiceTest {
    private final MpaService mpaService;

    @Test
    @DisplayName("Успешное добавление MPA")
    public void testFirstRow() {
        MPA mpa = mpaService.getMpaById(1L);
        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    @DisplayName("Сверка кол-ва добавленных MPA")
    public void getAllTest() {
        Collection<MPA> list = mpaService.getAll();
        assertEquals(list.size(), 5);
    }
}