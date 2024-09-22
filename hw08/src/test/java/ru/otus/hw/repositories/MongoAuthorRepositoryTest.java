package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import ru.otus.hw.models.Author;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Mongo repository для работы с авторами")
@DataMongoTest
public class MongoAuthorRepositoryTest {
    @Autowired
    private MongoAuthorRepository repository;


    @DisplayName("должен загружать автора по id")
    @Test
    void shouldReturnCorrectAuthorById() {
        var actualAuthor = repository.findById("1");
        var expectedAuthor = new Author("1", "Author_1");
        assertThat(actualAuthor).isPresent()
                .get()
                .isEqualTo(expectedAuthor);
    }

    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnCorrectAuthorsList() {
        var actualAuthors = repository.findAll();
        assertThat(actualAuthors).hasSize(3);
        assertThat(actualAuthors)
                .allMatch(author -> !author.getId().isBlank() && !author.getId().isEmpty());
        assertThat(actualAuthors)
                .extracting(Author::getFullName)
                .contains("Author_1", "Author_2", "Author_3");
    }
}
