package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Mongo repository для работы с комментариями")
@DataMongoTest
public class MongoCommentRepositoryTest {
    @Autowired
    private MongoCommentRepository repository;

    private static List<Comment> comments = new ArrayList<>();

    @BeforeAll
    public static void setup() {
        var authors = IntStream.rangeClosed(1, 3)
                .mapToObj(i -> new Author(String.valueOf(i), "Author_" + i))
                .toList();
        var genres = IntStream.rangeClosed(1, 6)
                .mapToObj(i -> new Genre(String.valueOf(i), "Genre_" + i))
                .toList();

        var genresForBooks = List.of(
                List.of(genres.get(0), genres.get(1)),
                List.of(genres.get(2), genres.get(3)),
                List.of(genres.get(4), genres.get(5))
        );

        var books = IntStream.rangeClosed(0, genresForBooks.size() - 1)
                .mapToObj(i -> new Book(String.valueOf(i + 1),
                        "Books_" + (i + 1),
                        authors.get(i),
                        genresForBooks.get(i)))
                .toList();

        comments = IntStream.rangeClosed(1, 4)
                .mapToObj(i -> new Comment(String.valueOf(i),
                        "Great book, really enjoyed it!_" + i,
                        books.get(0)))
                .toList();
        System.out.println();
    }

    @Test
    @DisplayName("должен загружать комментарий по id")
    void shouldReturnCorrectCommentById() {
        var actualComment = repository.findById(comments.get(0).getId());
        assertThat(actualComment).isPresent()
                .get()
                .isEqualTo(comments.get(0));
    }

    @Test
    @DisplayName("должен загружать список комментариев исходя из id книги")
    void shouldReturnCorrectCommentsList() {
        var actualComments = repository.findByBookId(comments.get(0).getBook().getId());
        assertThat(actualComments).isEqualTo(comments);
    }

    @Test
    @DisplayName("должен сохранять новый комментарий")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void shouldSaveNewComment() {
        var expectedComment = new Comment("newComment", comments.get(0).getBook());
        var returnedComment = repository.save(expectedComment);
        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() != null)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);
        System.out.println();
        assertThat(repository.findById(expectedComment.getId()))
                .isPresent()
                .get()
                .isEqualTo(expectedComment);
    }

    @Test
    @DisplayName("должен сохранять измененный комментарий")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void shouldSaveUpdatedBook() {
        var commentId = comments.get(0).getId();
        var expectedComment = new Comment(commentId, "UpdatedBook", comments.get(0).getBook());
        assertThat(repository.findById(commentId))
                .isPresent()
                .get()
                .isNotEqualTo(expectedComment);

        var returnedComment = repository.save(expectedComment);
        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() != null)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

        assertThat(repository.findById(commentId))
                .isPresent()
                .get()
                .isEqualTo(expectedComment);
    }

    @Test
    @DisplayName("должен удалять комментарий по id ")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    void shouldDeleteBook() {
        var commentId = comments.get(0).getId();
        assertThat(repository.findById(commentId)).isPresent();
        repository.deleteById(commentId);
        assertThat(repository.findById(commentId)).isNotPresent();
    }
}
