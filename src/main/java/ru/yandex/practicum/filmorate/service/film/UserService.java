package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage us;

    public void addUser(User film) {
        us.addUser(film);
    }

    public void updateUser(User film) {
        us.updateUser(film);
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

    public void addFriend(int id, int userId) {
        us.addFriend(id, userId);
    }

    public void removeFriend(int id, int userId) {
        us.removeFriend(id, userId);
    }

    public boolean checkUser(int id) {
        return us.checkUser(id);
    }
}
