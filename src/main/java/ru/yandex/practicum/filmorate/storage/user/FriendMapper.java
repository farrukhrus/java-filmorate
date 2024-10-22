package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friend;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendMapper implements RowMapper<Friend> {
    @Override
    public Friend mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Friend.builder()
                .userId(resultSet.getInt("user_id"))
                .friend(resultSet.getInt("friend_id"))
                .build();
    }
}
