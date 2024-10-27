package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
