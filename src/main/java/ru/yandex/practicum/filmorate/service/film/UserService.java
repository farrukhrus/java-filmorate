package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage us;

    public User addUser(User film) {
        return us.addUser(film);
    }

    public User updateUser(User film) {
        return us.updateUser(film);
    }

    public Collection<User> getAll() {
        return us.getAll();
    }

    public Collection<User> getFriends(int id) {
        return us.getFriends(id);
    }

    public Collection<User> getCommonFriends(int id, int otherId) {
        return us.getCommonFriends(id, otherId);
    }

    public User addFriend(int id, int userId) {
        return us.addFriend(id, userId);
    }

    public User removeFriend(int id, int userId) {
        return us.removeFriend(id, userId);
    }

    public boolean checkUser(int id) {
        return us.checkUser(id);
    }
}
