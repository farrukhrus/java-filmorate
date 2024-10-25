package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MPA {
    private Long id;
    @NotBlank(message = "Название MPA не заполнено")
    private String name;
}