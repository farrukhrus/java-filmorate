package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    void addUser(User user);

    void updateUser(User user);

    User getUser(int id);

    Collection<User> getAll();

    Collection<User> getFriends(int id);

    Collection<User> getCommonFriends(int userA, int userB);

    void addFriend(int id, int userId);

    void removeFriend(int id, int userId);

    boolean checkUser(int id);
}
