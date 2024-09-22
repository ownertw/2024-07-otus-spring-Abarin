package ru.otus.hw.listeners;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.repositories.MongoBookRepository;
import ru.otus.hw.repositories.MongoCommentRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(MongoBookEventListener.class)
@DisplayName("Проверка listeners для BookRepository")
public class BookEventListenerTest {
    @Autowired
    private MongoCommentRepository commentRepository;

    @Autowired
    private MongoBookRepository bookRepository;

    @Test
    @DisplayName("При удалении книги должен удалиться комментарий")
    void shouldRemoveCommentFromBooksWhenBookIsDeleted(){
        var commentsBeforeDeleteBook = commentRepository.findAll();
        assertThat(commentsBeforeDeleteBook).hasSize(4);

        bookRepository.deleteById("1");

        var commentsAfterDeleteBook = commentRepository.findAll();
        assertThat(commentsAfterDeleteBook).hasSize(0);
    }
}
