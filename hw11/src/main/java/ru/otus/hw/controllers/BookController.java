package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDtoIds;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.MongoAuthorRepository;
import ru.otus.hw.repositories.MongoBookRepository;
import ru.otus.hw.repositories.MongoGenreRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {

    private final MongoAuthorRepository authorRepository;

    private final MongoGenreRepository genreRepository;

    private final MongoBookRepository bookRepository;

    @GetMapping("/api/books/")
    public Flux<Book> getBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/api/books/{id}")
    public Mono<Book> getBook(@PathVariable("id") String id) {
        return bookRepository.findById(id);
    }

    @DeleteMapping("/api/books/{id}")
    public Mono<ResponseEntity<Void>> deleteBook(@PathVariable("id") String id) {
        return bookRepository.deleteById(id).map(ResponseEntity::ok);
    }

    @PutMapping("/api/books/{id}")
    public Mono<ResponseEntity<Book>> updateBook(@PathVariable("id") String id,
                                                 @Valid @RequestBody BookDtoIds bookDtoIds) {
        return bookRepository.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                    return createEntityBookWithoutId(bookDtoIds)
                            .doOnNext(updatedBook -> updatedBook.setId(id))
                            .flatMap(bookRepository::save)
                            .map(ResponseEntity::ok);
                });
    }

    @PostMapping("/api/books/")
    public Mono<ResponseEntity<Book>> saveBook(@RequestBody @Valid BookDtoIds bookDtoIds) {
        return createEntityBookWithoutId(bookDtoIds)
                .flatMap(bookRepository::save)
                .map(savedBook -> ResponseEntity.status(HttpStatus.CREATED).body(savedBook))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private Mono<Book> createEntityBookWithoutId(BookDtoIds bookDtoIds) {
        Book book = new Book();
        book.setTitle(bookDtoIds.getTitle());

        Mono<Author> authorMono = authorRepository.findById(bookDtoIds.getAuthorId());
        Mono<List<Genre>> genresMono = genreRepository.findAllById(bookDtoIds.getGenresIds()).collectList();

        return Mono.zip(authorMono, genresMono)
                .flatMap(tuple -> {
                    Author author = tuple.getT1();
                    List<Genre> genres = tuple.getT2();
                    book.setAuthor(author);
                    book.setGenres(genres);
                    return Mono.just(book);
                });
    }
}