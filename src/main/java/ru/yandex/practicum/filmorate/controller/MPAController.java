package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.film.MpaService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mpa")
public class MPAController {
    private final MpaService service;

    @GetMapping()
    public List<MPA> getList() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MPA getById(@PathVariable Long id) {
        return service.getMpaById(id);
    }
}