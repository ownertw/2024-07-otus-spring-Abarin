package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

@DisplayName("Mongo repository для работы с книгами")
@DataMongoTest
class MongoBookRepositoryTest {

    @Autowired
    private MongoBookRepository repository;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        dbAuthors = IntStream.range(1, 4).boxed()
                .map(id -> new Author(String.valueOf(id), "Author_" + id))
                .toList();
        dbGenres = IntStream.range(1, 7).boxed()
                .map(id -> new Genre(String.valueOf(id), "Genre_" + id))
                .toList();
        dbBooks = IntStream.range(1, 3).boxed()
                .map(id -> new Book(String.valueOf(id-1),
                        "Books_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)
                ))
                .toList();
    }

    @Test
    @DisplayName("должен загружать книгу по id")
    void shouldReturnCorrectBookById() {
        StepVerifier.create(repository.findById("0"))
                .expectNext(dbBooks.get(0))
                .verifyComplete();
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        StepVerifier.create(repository.findAll())
                .expectNextCount(3)
                .verifyComplete();
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldSaveNewBook() {
        var expectedBook = new Book("BookTitle_10500", dbAuthors.get(0),
                List.of(dbGenres.get(0), dbGenres.get(2)));

        StepVerifier.create(repository.save(expectedBook))
                .expectNextMatches(book -> book != null && book.getId() != null)
                .verifyComplete();

        StepVerifier.create(repository.findById(expectedBook.getId()))
                .expectNext(expectedBook)
                .verifyComplete();
    }


    @DisplayName("должен удалять книгу по id ")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldDeleteBook() {
        StepVerifier.create(repository.deleteById("0"))
                .verifyComplete();

        StepVerifier.create(repository.findById("0"))
                .expectNextCount(0)
                .verifyComplete();
    }
}