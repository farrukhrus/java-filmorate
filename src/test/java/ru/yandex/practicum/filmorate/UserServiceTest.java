package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.UserService;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest {
    private final UserService us;
    private final User user1 = new User("tmp@mail.com", "tmpLogin", "Vova", LocalDate.now());
    private final User user2 = new User("tmp2@mail.com", "tmpLogin2", "Vova", LocalDate.now());

    @BeforeEach
    public void addUser() {
        us.addUser(user1);
        us.addUser(user2);
    }

    @Test
    @DisplayName("Успешное добавление пользователя")
    public void testAddUser() {
        assertEquals(1, user1.getId(), "Пользователь добавлен");
    }
}