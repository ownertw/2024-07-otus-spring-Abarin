package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import ru.otus.hw.dto.BookDtoIds;
import ru.otus.hw.models.Book;

import java.time.Duration;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @LocalServerPort
    private int port;

    @Test
    void shouldReturnAllBooks() {
        var client = WebClient.create(String.format("http://localhost:%d", port));

        var result = client
                .get().uri("/api/books/")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(3))
                .block();

        assertThat(result).containsAnyOf("Books_3", "Books_2", "Books_1");
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldDeleteBook() {
        var client = WebClient.create(String.format("http://localhost:%d", port));

        var response = client
                .delete().uri("/api/books/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .timeout(Duration.ofSeconds(3))
                .block();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturnOneBook() {
        var client = WebClient.create(String.format("http://localhost:%d", port));

        var result = client
                .get().uri("/api/books/1")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(3))
                .block();

        assertThat(result).contains("Books_2", "Genre_3", "Author_2");
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldUpdateBookTitle() {
        var client = WebClient.create(String.format("http://localhost:%d", port));

        var bookIds = new BookDtoIds();
        bookIds.setTitle("New Book Title");
        bookIds.setAuthorId("3");
        bookIds.setGenresIds(Set.of("1", "3"));

        var result = client
                .get().uri("/api/books/1")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Book.class)
                .timeout(Duration.ofSeconds(3))
                .block();

        assertThat(result.getTitle()).isNotEqualTo(bookIds.getTitle());

        result = client
                .put().uri("/api/books/1")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(bookIds)
                .retrieve()
                .bodyToMono(Book.class)
                .timeout(Duration.ofSeconds(3))
                .block();

        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getTitle()).isEqualTo(bookIds.getTitle());
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldCreateNewBook() {
        var client = WebClient.create(String.format("http://localhost:%d", port));

        var bookIds = new BookDtoIds();
        bookIds.setTitle("New Book Entity");
        bookIds.setAuthorId("3");
        bookIds.setGenresIds(Set.of("1", "3"));

        var responseBeforeSave = client
                .get().uri("/api/books/")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(3))
                .block();

        assertThat(responseBeforeSave).doesNotContain(bookIds.getTitle());

        var response = client
                .post().uri("/api/books/")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(bookIds)
                .retrieve()
                .bodyToMono(Book.class)
                .timeout(Duration.ofSeconds(3))
                .block();

        assertThat(response.getId()).isNotNull();
        assertThat(response.getTitle()).isEqualTo(bookIds.getTitle());
    }

}
