package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Primary
@Slf4j
@Component("dbUserStorage")
@RequiredArgsConstructor
public class DBUserStorage implements UserStorage {
    private final FriendMapper friendMapper;
    private final UserMapper userMapper;

    private final JdbcTemplate jdbcTemplate;
    String errMessage;

    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM users WHERE ID = ?";
    private static final String SELECT_USERS_BY_IDS_QUERY = "SELECT * FROM users WHERE ID IN (?)";
    private static final String SELECT_USERS_QUERY = "SELECT * FROM users";
    private static final String INSERT_USER_QUERY =
            "INSERT INTO users (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY =
            "UPDATE users SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE ID = ?";
    private static final String ADD_FRIEND_QUERY = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
    private static final String GET_FRIENDS_QUERY = "SELECT * FROM friends WHERE user_id = ?";
    private static final String REMOVE_FRIEND_QUERY = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
    private static final String IN_QUERY = "SELECT * FROM users WHERE ID IN (%s)";

    @Override
    public void addUser(User user) {
        log.info("Создание пользователя с логином {}", user.getLogin());
        log.trace(user.toString());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_USER_QUERY, new String[]{"ID"});

            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));

            return ps;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        log.info("Пользователь с логином {} успешно создан с ID={}", user.getLogin(), user.getId());
    }

    @Override
    public void updateUser(User user) {
        log.info("Обновление пользователя {}", user.getId());
        log.trace(user.toString());

        if (getUser(user.getId()) == null) {
            errMessage = "Id должен быть указан";
            log.error(errMessage);
            throw new NotFoundException(errMessage);
        }

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(UPDATE_USER_QUERY);

            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            ps.setLong(5, user.getId());
            return ps;
        });
        log.info("Пользователь с ID={} успешно обновлен", user.getId());
    }

    @Override
    public User getUser(int userId) {
        log.info("Получение пользователя по ID {}", userId);
        List<User> result = jdbcTemplate.query(SELECT_BY_ID_QUERY, userMapper, userId);
        if (result.isEmpty()) {
            errMessage = "Пользователь не найден";
            log.error(errMessage);
            throw new NotFoundException(errMessage);
        }
        return result.getFirst();
    }

    @Override
    public List<User> getAll() {
        log.info("Получение всего списка пользователей");
        List<User> result = jdbcTemplate.query(SELECT_USERS_QUERY, userMapper);
        if (result.isEmpty()) {
            log.error("Не найдено ни одного пользователя");
            return null;
        }
        return result;
    }

    @Override
    public void addFriend(int user, int friend) {
        log.info("Добавление в друзья пользователю ID = {} пользователя с ID = {}", user, friend);
        if (getUser(user) == null || getUser(friend) == null) {
            errMessage = "Пользователь не найден";
            log.error(errMessage);
            throw new NotFoundException(errMessage);
        }
        jdbcTemplate.update(ADD_FRIEND_QUERY, user, friend);
    }

    @Override
    public void removeFriend(int user, int friend) {
        log.info("Удаление из друзей пользователя ID = {} пользователя с ID = {}", user, friend);
        if (getUser(user) == null || getUser(friend) == null) {
            errMessage = "Пользователь не найден";
            log.error(errMessage);
            throw new NotFoundException(errMessage);
        }
        jdbcTemplate.update(REMOVE_FRIEND_QUERY, user, friend);
    }

    @Override
    public boolean checkUser(int id) {
        return false;
    }

    @Override
    public ArrayList<User> getFriends(int user) {
        log.info("Получение списка друзей пользователя c ID = {}", user);

        if (getUser(user) == null) {
            throw new NotFoundException(errMessage);
        }

        List<Friend> result = jdbcTemplate.query(GET_FRIENDS_QUERY, friendMapper, user);
        List<String> ids = new ArrayList<>();

        if (result.isEmpty()) {
            errMessage = "Пользователь с id = " + user + " не найден";
            log.error(errMessage);
            return new ArrayList();
        }

        for (Friend friend : result) {
            ids.add(String.valueOf(friend.getFriend()));
        }

        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));

        return (ArrayList<User>) jdbcTemplate.query(
                String.format(IN_QUERY, inSql),
                userMapper,
                ids.toArray()
        );
    }

    @Override
    public ArrayList<User> getCommonFriends(int user, int friend) {
        log.info("Получение списка общих друзей пользователей c ID = {} и {}", user, friend);

        List<Friend> friendsList1 = jdbcTemplate.query(GET_FRIENDS_QUERY, friendMapper, user);
        List<Friend> friendsList2 = jdbcTemplate.query(GET_FRIENDS_QUERY, friendMapper, friend);
        ArrayList<Friend> friendsList = new ArrayList<>();
        StringBuilder friendsIds = new StringBuilder();

        for (Friend friendItem : friendsList1) {
            int id = friendItem.getFriend();
            Friend containFriend = Friend.builder()
                    .userId(friend)
                    .friend(id)
                    .build();

            if (friendsList2.contains(containFriend)) {
                friendsList.add(friendItem);
            }
        }

        for (Friend item : friendsList) {
            friendsIds.append(item.getFriend()).append(",");
        }
        friendsIds = new StringBuilder(friendsIds.substring(0, friendsIds.length() - 1));
        return (ArrayList<User>) jdbcTemplate.query(SELECT_USERS_BY_IDS_QUERY, userMapper, friendsIds.toString());
    }
}