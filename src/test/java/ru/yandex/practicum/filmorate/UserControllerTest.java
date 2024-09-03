package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.*;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {
    private UserController uc;
    User user1;
    User user2;

    @BeforeEach
    public void beforeEach() {
        uc = new UserController();
        user1 = new User();
        user2 = new User();

        user1.setEmail("test@gmail.com");
        user1.setLogin("test");
        user1.setName("Test");
        user1.setBirthday(LocalDate.of(1991, 1, 16));
        uc.create(user1);
    }

    @Test
    @DisplayName("Успешное добалвение пользователя")
    public void testAddUser() {
        assertEquals(1, user1.getId(), "Польватель добавлен");
    }

    @Test
    @DisplayName("Неуспешное обновление фильма с указанием отрицательного ID")
    public void testNegativeIdOnUpdate() {
        user2.setId(-1);
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            uc.update(user2);
        });
        assertEquals("Пользователь с id = -1 не найден", exception.getMessage(),
                "ID фильма не найден");
    }

    @Test
    @DisplayName("Если имя пользователя равно NULL, то сделать равным значению поля логин")
    public void testCreateUserWithoutName() {
        user2.setLogin("test2");
        uc.create(user2);
        assertEquals(user2.getLogin(), user2.getName(), "Логин == Имя");
    }
}