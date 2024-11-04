package ru.otus.hw.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.MongoAuthorRepository;
import ru.otus.hw.repositories.MongoBookRepository;
import ru.otus.hw.repositories.MongoGenreRepository;

import java.util.List;
import java.util.stream.IntStream;

@ChangeLog(order = "000")
public class LibraryMongoDbChangelogs {
    private final List<Author> authors;

    private final List<Genre> genres;

    public LibraryMongoDbChangelogs() {
        this.authors = IntStream.range(1, 4)
                .mapToObj(i -> new Author(new ObjectId().toString(), "Author_" + i)).toList();
        this.genres = IntStream.range(1, 7)
                .mapToObj(i -> new Genre(new ObjectId().toString(), "Genre_" + i)).toList();
    }

    @ChangeSet(order = "000", id = "dropDB", author = "owner_va", runAlways = true)
    public void dropDB(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "001", id = "initAuthors", author = "owner_va", runAlways = true)
    public void initAuthors(MongoAuthorRepository repository) {
        repository.saveAll(authors).subscribe();
    }

    @ChangeSet(order = "002", id = "initGenres", author = "owner_va", runAlways = true)
    public void initGenres(MongoGenreRepository repository) {
        repository.saveAll(genres).subscribe();
    }

    @ChangeSet(order = "003", id = "initBooks", author = "owner_va", runAlways = true)
    public void initBooks(MongoBookRepository repository) {
        List<List<Genre>> genresForBooks = List.of(
                List.of(genres.get(0), genres.get(1)),
                List.of(genres.get(2), genres.get(3)),
                List.of(genres.get(4), genres.get(5))
        );
        Flux<Book> bookFlux = Flux.fromStream(IntStream.range(0, 3)
                .mapToObj(i -> new Book(new ObjectId().toString(),
                        "Books_" + (i + 1),
                        authors.get(i), genresForBooks.get(i))));

        repository.saveAll(bookFlux).subscribe();
    }
}
