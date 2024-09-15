package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public User addUser(User user);

    public User updateUser(User user);

    public User getUser(int id);

    public Collection<User> getAll();

    public Collection<User> getFriends(int id);

    public Collection<User> getCommonFriends(int userA, int userB);

    public User addFriend(int id, int userId);

    public User removeFriend(int id, int userId);

    public boolean checkUser(int id);
}
