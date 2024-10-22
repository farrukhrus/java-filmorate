package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MPA {
    private Long id;
    private String name;
}