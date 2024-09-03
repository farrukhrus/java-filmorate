package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
@NotNull
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получение всего списка пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Создание пользователя с логином {}", user.getLogin());
        log.trace(user.toString());

        // если не задано поле Name
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь с логином {} успешно создан с ID={}", user.getLogin(), user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Обновление пользователя {}", newUser.getId());
        log.trace(newUser.toString());
        String errMessage;

        if (newUser.getId() == 0) {
            errMessage = "Id должен быть указан";
            log.error(errMessage);
            throw new ConditionsNotMetException(errMessage);
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (!(newUser.getName() == null || newUser.getName().isBlank())) {
                oldUser.setName(newUser.getName());
            }
            // если поле Name пришло null
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                oldUser.setName(newUser.getLogin());
            }
            if (!newUser.getBirthday().isAfter(LocalDate.now())) {
                oldUser.setBirthday(newUser.getBirthday());
            }
            oldUser.setLogin((newUser.getLogin()));
            oldUser.setEmail(newUser.getEmail());
            log.info("Пользователь с ID={} успешно обновлен", newUser.getId());
            return oldUser;
        }
        log.error("Не найден полльзователь с ID = {}", newUser.getId());
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    // вспомогательный метод для генерации идентификатора нового поста
    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        log.trace("Очередной ID пользователя: {}", currentMaxId);
        return ++currentMaxId;
    }
}