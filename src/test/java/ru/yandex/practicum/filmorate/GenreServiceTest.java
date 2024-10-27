package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.film.GenreService;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GenreServiceTest {
    private final GenreService genreService;

    @Test
    @DisplayName("Успешное заполнение таблицы")
    public void testFirstRow() {
        Genre genre = genreService.getById(1L);
        assertThat(genre).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия");
    }
}