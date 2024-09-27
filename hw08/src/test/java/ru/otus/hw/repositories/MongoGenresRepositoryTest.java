package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import ru.otus.hw.models.Genre;

import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Mongo repository для работы с жанрами")
@DataMongoTest
public class MongoGenresRepositoryTest {
    @Autowired
    private MongoGenreRepository repository;

    @DisplayName("должен загружать жанры по ids")
    @Test
    void shouldReturnCorrectGenresById() {
        var ids = Set.of("1", "2", "3");
        var actualGenres = repository.findAllById(ids);
        var expectedGenres = IntStream.rangeClosed(1, 3)
                .mapToObj(i -> new Genre(String.valueOf(i), "Genre_" + i))
                .toList();
        assertThat(actualGenres).isEqualTo(expectedGenres);
    }

    @DisplayName("должен загружать список всех жарнов")
    @Test
    void shouldReturnCorrectGenresList() {
        var actualGenres = repository.findAll();
        var expectedGenres = IntStream.rangeClosed(1, 6)
                .mapToObj(i -> new Genre(String.valueOf(i), "Genre_" + i))
                .toList();

        assertThat(actualGenres).containsExactlyElementsOf(expectedGenres);
    }
}
