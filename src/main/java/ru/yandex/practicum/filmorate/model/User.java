package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class User {
    private Integer id;
    @Email(message = "Некорректный формат поля email")
    @NotBlank(message = "Поле email не может быть пустым")
    private String email;
    @NotBlank(message = "Логин не заполнен")
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    private final Set<Integer> friends = new HashSet<>();

    public Integer getFriends() {
        return friends.size();
    }

    @JsonIgnore
    public Set<Integer> getFriendsIds() {
        return friends;
    }

    public void addFriend(int userId) {
        friends.add(userId);
    }

    public void removeFriend(int userId) {
        friends.remove(userId);
    }
}
