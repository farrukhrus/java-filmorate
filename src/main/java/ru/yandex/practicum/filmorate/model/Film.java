package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private Integer id;
    @NotBlank(message = "Поле с именем должно быть заполнено")
    private String name;
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;
    @NotNull(message = "Дата выпуска должна быть заполнена")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
}
