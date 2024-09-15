package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.time.Month;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmControllerTest {
	Film film;
	Film film2;
	FilmController fc;
	FilmStorage fst;
	FilmService fs;

	@BeforeEach
	public void beforeEach() {
		film = new Film();
		film2 = new Film();
		fst = new InMemoryFilmStorage();
		fs = new FilmService(fst);
		fc = new FilmController(fs);

		film.setName("С легким паром");
		film.setDescription("Про баню и веники");
		film.setReleaseDate(LocalDate.of(1991, 01, 16));
		film.setDuration(100);
		fc.addFilm(film);
	}

	@Test
	@DisplayName("Успешное добавление фильма")
	public void testAddFilm() {
		assertEquals(1, film.getId(), "Фильм добавлен");
	}

	@Test
	@DisplayName("Неуспешное обновление фильма с указанием отрицательного ID")
	public void testNegativeIdOnUpdate() {
		film2.setId(-1);
		Exception exception = assertThrows(NotFoundException.class, () -> {
			fc.updateFilm(film2);
		});
		assertEquals("Фильм с id = -1 не найден", exception.getMessage(), "ID фильма не найден");
	}

	@Test
	@DisplayName("Неуспешное обновление фильма с указанием некорректной даты выпуска")
	public void testInvalidReleaseDateOnUpdate() {
		String exceptionText = "";
		film2.setId(film.getId());
		film2.setReleaseDate(LocalDate.of(1815, Month.DECEMBER, 28));
		Exception exception = assertThrows(ConditionsNotMetException.class, () -> {
			fc.updateFilm(film2);
		});
		assertEquals("Дата выпуска фильма должна быть не раньше 28 декабря 1895 года",
				exception.getMessage(), "Дата выпуска не обновилась");
	}
}