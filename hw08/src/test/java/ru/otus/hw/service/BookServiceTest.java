package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.BookServiceImpl;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис книг")
@DataMongoTest
@Import({BookServiceImpl.class,
        BookConverter.class,
        AuthorConverter.class,
        GenreConverter.class})
public class BookServiceTest {
    @Autowired
    private BookService bookService;

    @Test
    @DisplayName("должен возвращать все книги")
    void shouldReturnCorrectBooksList() {
        var actualBook = bookService.findAll();
        assertThat(actualBook).isEqualTo(getExpectedBooks());
    }

    @Test
    @DisplayName("должен возвращать книгу по id")
    void shouldReturnCorrectBookById() {
        bookService.findById("1").ifPresent(bookDto -> assertThat(bookDto).isEqualTo(getExpectedBooks().get(0)));
    }

    @Test
    @DisplayName("должен добавлять новую книгу")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void shouldSaveNewBook() {
        var author = new AuthorDto("1", "Author_1");
        var genre = new GenreDto("2", "Genre_2");
        var expectedBook = new BookDto(null, "BookTitle_10500", author,
                List.of(genre));

        BookDto actualBook = bookService.insert("BookTitle_10500", "1", Set.of("2"));

        assertThat(actualBook.getId())
                .isNotNull()
                .isNotEmpty()
                .isNotBlank();
        assertThat(actualBook)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedBook);
    }

    @Test
    @DisplayName("должен изменять книгу")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void shouldUpdateBook() {
        bookService.findById("1").ifPresent(bookDto -> {
            BookDto beforeUpdate = bookService.update("1", "BookTitle_Updated", "1", Set.of("2"));
            assertThat(bookDto).isNotEqualTo(beforeUpdate);
        });
        bookService.findById("1").ifPresent(bookDto -> {
            assertThat(bookDto.getTitle()).isEqualTo("BookTitle_Updated");
        });
    }

    @Test
    @DisplayName("должен удалять книгу")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void shouldDeleteBook() {
        bookService.deleteById("1");
        var actualBooks = bookService.findAll();
        assertThat(actualBooks).doesNotContain(getExpectedBooks().get(0));
        assertThat(actualBooks).containsExactly(getExpectedBooks().get(1), getExpectedBooks().get(2));
    }

    public List<BookDto> getExpectedBooks() {
        return List.of(
                new BookDto("1", "Books_1",
                        new AuthorDto("1", "Author_1"),
                        List.of(new GenreDto("1", "Genre_1"), new GenreDto("2", "Genre_2"))),
                new BookDto("2", "Books_2",
                        new AuthorDto("2", "Author_2"),
                        List.of(new GenreDto("3", "Genre_3"), new GenreDto("4", "Genre_4"))),
                new BookDto("3", "Books_3",
                        new AuthorDto("3", "Author_3"),
                        List.of(new GenreDto("5", "Genre_5"), new GenreDto("6", "Genre_6")))
        );
    }
}
