package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class User {
    int id;
    @Email(message = "Некорректный формат поля email")
    @NotBlank(message = "Поле email не может быть пустым")
    String email;
    @NotBlank(message = "Логин не заполнен")
    String login;
    String name;
    LocalDate birthday;
}
