package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final GenreRepository genreRepository;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Optional<Book> findById(long id) {
        var query = """
                SELECT b.id AS book_id,
                       b.title AS book_title,
                       a.id AS author_id,
                       a.full_name AS author_name,
                       g.id AS genre_id,
                       g.name AS genre_name
                FROM books b
                JOIN authors a ON b.author_id = a.id
                JOIN books_genres bg ON bg.book_id = b.id
                JOIN genres g ON g.id = bg.genre_id
                WHERE b.id = :id
                """;
        Book book = namedParameterJdbcTemplate.query(query, Map.of("id", id), new BookResultSetExtractor());
        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        namedParameterJdbcTemplate.update(
                "DELETE FROM books WHERE id = :book_id",
                Map.of("book_id", id)
        );
    }

    private List<Book> getAllBooksWithoutGenres() {
        String query = """
                SELECT b.id AS book_id,
                       b.title AS book_title,
                       a.id AS author_id,
                       a.full_name AS author_name
                FROM books b
                JOIN authors a ON b.author_id = a.id
                """;
        return namedParameterJdbcTemplate.query(query, new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return namedParameterJdbcTemplate.query(
                "SELECT book_id, genre_id FROM books_genres",
                (rs, rowNum) -> new BookGenreRelation(
                        rs.getLong("book_id"),
                        rs.getLong("genre_id")
                )
        );
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        var genresByBookId = groupGenresFromRelationByBookId(relations, genres);
        setGenresToBooks(booksWithoutGenres, genresByBookId);
    }

    private Map<Long, List<Genre>> groupGenresFromRelationByBookId(List<BookGenreRelation> relations,
                                                                   List<Genre> genres) {
        Map<Long, Genre> genresById = genres.stream().collect(Collectors.toMap(Genre::getId, Function.identity()));
        return relations.stream()
                .collect(Collectors.groupingBy(
                        BookGenreRelation::bookId,
                        Collectors.mapping(relation -> genresById.get(relation.genreId()), Collectors.toList())
                ));
    }

    private void setGenresToBooks(List<Book> books, Map<Long, List<Genre>> genresByBookId) {
        books.forEach(book -> book.setGenres(genresByBookId.getOrDefault(book.getId(), Collections.emptyList())));
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValues(Map.of(
                "title", book.getTitle(),
                "author_id", book.getAuthor().getId()
        ));

        namedParameterJdbcTemplate.update("INSERT INTO books (title, author_id) VALUES (:title, :author_id)",
                parameterSource,
                keyHolder,
                new String[]{"id"});

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        var query = "UPDATE books SET title = :book_title, author_id = :author_id where id = :book_id";
        int resultUpdate = namedParameterJdbcTemplate.update(query,
                Map.of("book_id", book.getId(),
                        "book_title", book.getTitle(),
                        "author_id", book.getAuthor().getId()
                ));

        if (resultUpdate == 0) {
            throw new EntityNotFoundException("No update table books by book id: " + book.getId());
        }

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        var batchArgs = book.getGenres().stream()
                .map(genre -> new MapSqlParameterSource(Map.of(
                        "book_id", book.getId(),
                        "genre_id", genre.getId())))
                .toArray(SqlParameterSource[]::new);
        var query = "INSERT INTO books_genres (book_id, genre_id) VALUES (:book_id, :genre_id)";

        namedParameterJdbcTemplate.batchUpdate(query, batchArgs);
    }

    private void removeGenresRelationsFor(Book book) {
        namedParameterJdbcTemplate.update("DELETE FROM books_genres WHERE book_id = :book_id",
                Map.of("book_id", book.getId()));
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            var bookId = rs.getLong("book_id");
            var bookTitle = rs.getString("book_title");
            var authorId = rs.getLong("author_id");
            var authorFullName = rs.getString("author_name");

            Author author = new Author(authorId, authorFullName);

            return new Book(bookId, bookTitle, author, Collections.emptyList());
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            Book book = null;
            while (rs.next()) {
                if (book == null) {
                    book = new Book();
                    book.setId(rs.getLong("book_id"));
                    book.setTitle(rs.getString("book_title"));
                    var author = new Author(rs.getLong("author_id"),
                            rs.getString("author_name"));
                    book.setAuthor(author);
                    book.setGenres(new ArrayList<>());
                }
                var genre = new Genre(rs.getLong("genre_id"), rs.getString("genre_name"));
                book.getGenres().add(genre);
            }
            return book;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
