package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    int id;
    @NotBlank(message = "Поле с именем должно быть заполнено")
    String name;
    @NotBlank(message = "Описание должно быть заполнено")
    String description;
    LocalDate releaseDate;
    int duration;
}
