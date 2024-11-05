package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.MongoGenreRepository;

@RestController
@RequiredArgsConstructor
public class GenreController {

    private final MongoGenreRepository genreRepository;

    @GetMapping("/api/genres/")
    public Flux<Genre> getGenres() {
        return genreRepository.findAll();
    }
}
