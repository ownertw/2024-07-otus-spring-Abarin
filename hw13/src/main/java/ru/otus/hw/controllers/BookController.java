package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.otus.hw.dto.BookDtoIds;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    @GetMapping("/books/")
    public String getBooks(Model model) {
        var books = bookService.findAll();
        model.addAttribute("books", books);
        return "books";
    }

    @GetMapping("/books/{id}")
    public String getBook(@PathVariable("id") long id, Model model) {
        var book = bookService.findById(id);
        model.addAttribute("book", book);
        var authors = authorService.findAll();
        model.addAttribute("authors", authors);
        var genres = genreService.findAll();
        model.addAttribute("genres", genres);
        return "book";
    }

    @GetMapping("/books/add-form")
    public String addBook(Model model) {
        var authors = authorService.findAll();
        model.addAttribute("authors", authors);
        var genres = genreService.findAll();
        model.addAttribute("genres", genres);
        return "addedForm";
    }

    @PostMapping("/books/delete")
    public String deleteBook(Long bookId) {
        bookService.deleteById(bookId);
        return "redirect:/books/";
    }

    @PostMapping("/books/update")
    public String updateBook(@Valid @ModelAttribute("book") BookDtoIds bookDtoIds,
                             BindingResult bindingResult,
                             RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("errors", getErrorMessages(bindingResult));
            return "redirect:/books/" + bookDtoIds.getId();
        }
        bookService.update(bookDtoIds.getId(),
                bookDtoIds.getTitle(),
                bookDtoIds.getAuthorId(),
                bookDtoIds.getGenresIds());

        return "redirect:/books/" + bookDtoIds.getId();
    }

    @PostMapping("/books/save")
    public String saveBook(@Valid @ModelAttribute("book") BookDtoIds bookDtoIds,
                           BindingResult bindingResult,
                           RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            attributes.addFlashAttribute("errors", getErrorMessages(bindingResult));
            return "redirect:/books/add-form";
        }

        var bookId = bookService.insert(bookDtoIds.getTitle(),
                bookDtoIds.getAuthorId(),
                bookDtoIds.getGenresIds()).getId();

        return "redirect:/books/" + bookId;
    }

    private List<String> getErrorMessages(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
    }
}
