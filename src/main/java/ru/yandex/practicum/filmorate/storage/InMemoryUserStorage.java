package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
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

    @Override
    public User updateUser(User user) {
        log.info("Обновление пользователя {}", user.getId());
        log.trace(user.toString());
        String errMessage;

        if (user.getId() == 0) {
            errMessage = "Id должен быть указан";
            log.error(errMessage);
            throw new ConditionsNotMetException(errMessage);
        }
        if (checkUser(user.getId())) {
            User oldUser = users.get(user.getId());
            if (!(user.getName() == null || user.getName().isBlank())) {
                oldUser.setName(user.getName());
            }
            // если поле Name пришло null
            if (user.getName() == null || user.getName().isBlank()) {
                oldUser.setName(user.getLogin());
            }
            if (user.getBirthday() != null && !user.getBirthday().isAfter(LocalDate.now())) {
                oldUser.setBirthday(user.getBirthday());
            }
            if (user.getLogin() != null) {
                oldUser.setLogin((user.getLogin()));
            }
            if (user.getEmail() != null) {
                oldUser.setEmail(user.getEmail());
            }
            log.info("Пользователь с ID={} успешно обновлен", user.getId());
            return oldUser;
        }
        log.error("Не найден полльзователь с ID = {}", user.getId());
        throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
    }

    @Override
    public Collection<User> getAll() {
        log.info("Получение всего списка пользователей");
        return users.values();
    }

    @Override
    public User getUser(int id) {
        return users.get(id);
    }

    @Override
    public Collection<User> getFriends(int id) {
        if (!checkUser(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        Set<Integer> commonFriendsIds = users.get(id).getFriendsIds();
        return commonFriendsIds.stream().map(users::get).toList();
    }

    @Override
    public Collection<User> getCommonFriends(int id, int otherId) {
        if (!checkUser(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        if (!checkUser(otherId)) {
            throw new NotFoundException("Пользователь с id = " + otherId + " не найден");
        }
        Set<Integer> commonFriendsIds = users.get(id).getFriendsIds();
        commonFriendsIds.retainAll(users.get(otherId).getFriendsIds());

        return commonFriendsIds.stream().map(users::get).toList();
    }

    @Override
    public User addFriend(int id, int userId) {
        if (!checkUser(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!checkUser(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        if (id == userId) {
            throw new NotFoundException("Нельзя добавить/удалить самого себя из друзей");
        }
        User userSrc = users.get(id);
        userSrc.addFriend(userId);

        User userTrg = users.get(userId);
        userTrg.addFriend(id);
        return userSrc;
    }

    @Override
    public User removeFriend(int id, int userId) {
        if (!checkUser(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        if (!checkUser(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        if (id == userId) {
            throw new NotFoundException("Нельзя добавить/удалить самого себя из друзей");
        }
        User userSrc = users.get(id);
        userSrc.removeFriend(userId);

        User userTrg = users.get(userId);
        userTrg.removeFriend(id);

        return userSrc;
    }

    // существует ли пользователь
    public boolean checkUser(int userId) {
        return users.containsKey(userId);
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
