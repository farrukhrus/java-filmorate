package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Primary
@Component("dbUserStorage")
@RequiredArgsConstructor
public class DBUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
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
    public User addUser(User user) {
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
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (getUser(user.getId()) == null) {
            throw new NotFoundException("Пользователь не найден");
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
        return user;
    }

    @Override
    public User getUser(int userId) {
        List<User> result = jdbcTemplate.query(SELECT_BY_ID_QUERY, this::userMapper, userId);
        if (result.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        return result.getFirst();
    }

    @Override
    public List<User> getAll() {
        List<User> result = jdbcTemplate.query(SELECT_USERS_QUERY, this::userMapper);
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    @Override
    public User addFriend(int user, int friend) {
        if (getUser(user) == null || getUser(friend) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        jdbcTemplate.update(ADD_FRIEND_QUERY, user, friend);
        return getUser(user);
    }

    @Override
    public User removeFriend(int user, int friend) {
        if (getUser(user) == null || getUser(friend) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        jdbcTemplate.update(REMOVE_FRIEND_QUERY, user, friend);
        return getUser(user);
    }

    @Override
    public boolean checkUser(int id) {
        return false;
    }

    @Override
    public ArrayList<User> getFriends(int user) {
        List<Friend> result = jdbcTemplate.query(GET_FRIENDS_QUERY, this::friendMapper, user);

        if (result.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<String> ids = new ArrayList<>();

        for (Friend friend : result) {
            ids.add(String.valueOf(friend.getFriend()));
        }

        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));

        return (ArrayList<User>) jdbcTemplate.query(
                String.format(IN_QUERY, inSql),
                this::userMapper,
                ids.toArray()
        );
    }

    @Override
    public ArrayList<User> getCommonFriends(int user, int friend) {
        List<Friend> friendsList1 = jdbcTemplate.query(GET_FRIENDS_QUERY, this::friendMapper, user);
        List<Friend> friendsList2 = jdbcTemplate.query(GET_FRIENDS_QUERY, this::friendMapper, friend);

        ArrayList<Friend> friendsList = new ArrayList<>();

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

        StringBuilder friendsIds = new StringBuilder();

        for (Friend item : friendsList) {
            friendsIds.append(item.getFriend()).append(",");
        }

        friendsIds = new StringBuilder(friendsIds.substring(0, friendsIds.length() - 1));

        return (ArrayList<User>) jdbcTemplate.query(SELECT_USERS_BY_IDS_QUERY, this::userMapper, friendsIds.toString());
    }

    private User userMapper(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("ID"))
                .email(resultSet.getString("EMAIL"))
                .login(resultSet.getString("LOGIN"))
                .name(resultSet.getString("NAME"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .build();
    }

    private Friend friendMapper(ResultSet resultSet, int rowNum) throws SQLException {
        return Friend.builder()
                .userId(resultSet.getInt("user_id"))
                .friend(resultSet.getInt("friend_id"))
                .build();
    }
}