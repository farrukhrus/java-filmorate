package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import java.time.LocalDate;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmServiceTest {
	private final FilmService fs;
	private Film film1;
	private Film film2;

	@BeforeEach
	public void beforeEach() {
		film1 = new Film("Test1",
				"description1",
				LocalDate.now(),
				100,
				new MPA(1L, null),
				Set.of(new Genre(1L, null))
		);

		fs.addFilm(film1);
	}

	@Test
	@DisplayName("Успешное добавление фильма")
	public void testAddFilm() {
		assertEquals(1, film1.getId(), "Фильм добавлен");
	}
}
