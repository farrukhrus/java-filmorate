package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

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
}
