package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BookController.class)
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @Test
    public void shouldGivenBooksList() throws Exception {
        given(bookService.findAll())
                .willReturn(getExpectedBooks());
        this.mockMvc.perform(get("/books/"))
                .andExpect(status().isOk())
                .andExpect(view().name("books"))
                .andExpect(model().attributeExists("books"))
                .andExpect(content().string(containsString(getExpectedBooks().get(0).getTitle())))
                .andExpect(content().string(containsString(getExpectedBooks().get(1).getTitle())))
                .andExpect(content().string(containsString(getExpectedBooks().get(2).getTitle())));
        verify(bookService, times(1)).findAll();
    }


    @Test
    public void shouldGivenBook() throws Exception {
        given(bookService.findById(1))
                .willReturn(getExpectedBooks().get(0));
        given(authorService.findAll())
                .willReturn(getExpectedAuthors());
        given(genreService.findAll())
                .willReturn(getExpectedGenres());
        this.mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("book"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"))
                .andExpect(content().string(containsString(getExpectedBooks().get(0).getTitle())))
                .andExpect(content().string(containsString(getExpectedAuthors().get(0).getFullName())))
                .andExpect(content().string(containsString(getExpectedGenres().get(0).getName())));
        verify(bookService, times(1)).findById(1);
        verify(authorService, times(1)).findAll();
        verify(genreService, times(1)).findAll();
    }

    @Test
    public void shouldReturnAddedBookFormAndBackgroundInformation() throws Exception {
        given(authorService.findAll())
                .willReturn(getExpectedAuthors());
        given(genreService.findAll())
                .willReturn(getExpectedGenres());
        mockMvc.perform(get("/books/add-form"))
                .andExpect(status().isOk())
                .andExpect(view().name("addedForm"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"))
                .andExpect(content().string(containsString(getExpectedAuthors().get(0).getFullName())))
                .andExpect(content().string(containsString(getExpectedGenres().get(0).getName())));
        verify(authorService, times(1)).findAll();
        verify(genreService, times(1)).findAll();
    }

    @Test
    public void shouldDeleteAndRedirected() throws Exception {
        long id = 1L;
        doNothing().when(bookService).deleteById(id);

        mockMvc.perform(get("/books/delete/{id}", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/"));
        verify(bookService, times(1)).deleteById(1);
    }

    @Test
    public void shouldUpdateBookAndRedirected() throws Exception {
        given(bookService.update(anyLong(),
                anyString(),
                anyLong(),
                anySet())).willReturn(getExpectedBooks().get(0));

        mockMvc.perform(post("/books/update")
                        .param("id", "1")
                        .param("title", "sdfsdf")
                        .param("authorId", "1")
                        .param("genresIds", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/1"));
        verify(bookService, times(1)).update(anyLong(),
                anyString(),
                anyLong(),
                anySet());
    }

    @Test
    public void shouldSaveNewBookAndRedirected() throws Exception {
        given(bookService.update(anyLong(),
                anyString(),
                anyLong(),
                anySet())).willReturn(getExpectedBooks().get(0));

        mockMvc.perform(post("/books/update")
                        .param("id", "0")
                        .param("title", "sdfsdf")
                        .param("authorId", "1")
                        .param("genresIds", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/0"));
        verify(bookService, times(1)).update(anyLong(),
                anyString(),
                anyLong(),
                anySet());
    }

    public List<BookDto> getExpectedBooks() {
        return List.of(
                new BookDto(1L, "BookTitle_1",
                        new AuthorDto(1L, "Author_1"),
                        List.of(new GenreDto(1L, "Genre_1"), new GenreDto(2L, "Genre_2"))),
                new BookDto(2L, "BookTitle_2",
                        new AuthorDto(2L, "Author_2"),
                        List.of(new GenreDto(3L, "Genre_3"), new GenreDto(4L, "Genre_4"))),
                new BookDto(3L, "BookTitle_3",
                        new AuthorDto(3L, "Author_3"),
                        List.of(new GenreDto(5L, "Genre_5"), new GenreDto(6L, "Genre_6")))
        );
    }

    public List<AuthorDto> getExpectedAuthors() {
        return List.of(
                new AuthorDto(1L, "Author_1"),
                new AuthorDto(2L, "Author_2"),
                new AuthorDto(3L, "Author_3")
        );
    }

    public List<GenreDto> getExpectedGenres() {
        return List.of(
                new GenreDto(1L, "Genre_1"),
                new GenreDto(2L, "Genre_2")
        );
    }
}
