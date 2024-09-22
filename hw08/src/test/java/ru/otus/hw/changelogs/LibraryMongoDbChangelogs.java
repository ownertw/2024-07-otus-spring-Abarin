package ru.otus.hw.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.MongoAuthorRepository;
import ru.otus.hw.repositories.MongoBookRepository;
import ru.otus.hw.repositories.MongoCommentRepository;
import ru.otus.hw.repositories.MongoGenreRepository;

import java.util.ArrayList;
import java.util.List;

@ChangeLog(order = "000")
public class LibraryMongoDbChangelogs {
    private final List<Author> authors = new ArrayList<>();

    private final List<Book> books = new ArrayList<>();

    private final List<Comment> comments = new ArrayList<>();

    private final List<Genre> genres = new ArrayList<>();

    @ChangeSet(order = "000", id = "dropDB", author = "owner_va", runAlways = true)
    public void dropDB(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "001", id = "initAuthors", author = "owner_va", runAlways = true)
    public void initAuthors(MongoAuthorRepository repository) {
        for (int i = 1; i <= 3; i++) {
            authors.add(new Author(String.valueOf(i), "Author_" + i));
        }
        repository.saveAll(authors);
    }

    @ChangeSet(order = "002", id = "initGenres", author = "owner_va", runAlways = true)
    public void initGenres(MongoGenreRepository repository) {
        for (int i = 1; i <= 6; i++) {
            genres.add(new Genre(String.valueOf(i), "Genre_" + i));
        }
        repository.saveAll(genres);
    }

    @ChangeSet(order = "003", id = "initBooks", author = "owner_va", runAlways = true)
    public void initBooks(MongoBookRepository repository) {
        var genresForBooks = List.of(
                List.of(genres.get(0), genres.get(1)),
                List.of(genres.get(2), genres.get(3)),
                List.of(genres.get(4), genres.get(5))
        );

        for (int i = 0; i < genresForBooks.size(); i++) {
            books.add(new Book(String.valueOf(i + 1), "Books_" + (i + 1), authors.get(i), genresForBooks.get(i)));
        }
        repository.saveAll(books);
    }

    @ChangeSet(order = "004", id = "initComments", author = "owner_va", runAlways = true)
    public void initComments(MongoCommentRepository repository) {
        for (int i = 1; i <= 4; i++) {
            comments.add(new Comment(String.valueOf(i), "Great book, really enjoyed it!_" + i, books.get(0)));
        }
        repository.saveAll(comments);
    }

}
