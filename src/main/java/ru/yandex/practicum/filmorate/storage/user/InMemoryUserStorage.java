package ru.yandex.practicum.filmorate.storage.user;

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
            if (user.getName() == null && user.getLogin() != null && user.getLogin().isBlank()) {
                oldUser.setName(user.getLogin());
            }
            if (user.getBirthday() != null && !user.getBirthday().isAfter(LocalDate.now())) {
                oldUser.setBirthday(user.getBirthday());
            }
            if (user.getLogin() != null && !user.getLogin().isBlank()) {
                oldUser.setLogin((user.getLogin()));
            }
            if (user.getEmail() != null && !user.getEmail().isBlank()) {
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
        log.info("Получение пользователя по ID {}", id);
        return users.get(id);
    }

    @Override
    public Collection<User> getFriends(int id) {
        log.info("Получение списка друзей пользователя c ID = {}", id);
        if (!checkUser(id)) {
            String errMessage = "Пользователь с id = " + id + " не найден";
            log.error(errMessage);
            throw new NotFoundException(errMessage);
        }
        Set<Integer> commonFriendsIds = users.get(id).getFriendsIds();
        return commonFriendsIds.stream().map(users::get).toList();
    }

    @Override
    public Collection<User> getCommonFriends(int id, int otherId) {
        log.info("Получение списка общих друзей пользователей c ID = {} и {}", id, otherId);
        validateUser(id, otherId);
        Set<Integer> commonFriendsIds = users.get(id).getFriendsIds();
        commonFriendsIds.retainAll(users.get(otherId).getFriendsIds());

        return commonFriendsIds.stream().map(users::get).toList();
    }

    @Override
    public User addFriend(int id, int userId) {
        log.info("Добавление в друзья пользователю ID = {} пользователя с ID = {}", id, userId);
        validateUser(id, userId);

        User userSrc = users.get(id);
        userSrc.addFriend(userId);

        User userTrg = users.get(userId);
        userTrg.addFriend(id);
        return userSrc;
    }

    @Override
    public User removeFriend(int id, int userId) {
        log.info("Удаление из друзей пользователя ID = {} пользователя с ID = {}", id, userId);
        validateUser(id, userId);

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

    private void validateUser(int id, int userId) {
        String errMessage;
        if (!checkUser(userId)) {
            errMessage = "Пользователь с id = " + userId + " не найден";
            log.error(errMessage);
            throw new NotFoundException(errMessage);
        }
        if (!checkUser(id)) {
            errMessage = "Пользователь с id = " + id + " не найден";
            log.error(errMessage);
            throw new NotFoundException(errMessage);
        }
        if (id == userId) {
            errMessage = "Нельзя добавить/удалить самого себя из друзей";
            log.error(errMessage);
            throw new NotFoundException(errMessage);
        }
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
