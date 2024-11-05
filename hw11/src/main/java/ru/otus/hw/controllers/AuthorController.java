package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.MongoAuthorRepository;

@RestController
@RequiredArgsConstructor
public class AuthorController {

    private final MongoAuthorRepository authorRepository;

    @GetMapping("/api/authors/")
    public Flux<Author> getAuthors() {
        return authorRepository.findAll();
    }
}
