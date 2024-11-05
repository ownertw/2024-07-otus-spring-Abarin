package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.test.StepVerifier;
import ru.otus.hw.models.Author;

@DisplayName("Mongo repository для работы с авторами")
@DataMongoTest
public class MongoAuthorRepositoryTest {

    @Autowired
    private MongoAuthorRepository repository;


    @DisplayName("должен загружать автора по id")
    @Test
    void shouldReturnCorrectAuthorById() {
        var expectedAuthor = new Author("1", "Author_1");

        StepVerifier.create(repository.findById("1"))
                .expectNext(expectedAuthor)
                .verifyComplete();
    }

    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnCorrectAuthorsList() {
        StepVerifier.create(repository.findAll())
                .expectNextCount(3)
                .verifyComplete();
    }
}
