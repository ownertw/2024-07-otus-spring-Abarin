package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.MongoBookRepository;
import ru.otus.hw.repositories.MongoCommentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final MongoCommentRepository commentRepository;

    private final MongoBookRepository bookRepository;

    private final CommentConverter commentConverter;

    @Override
    public Optional<CommentDto> findById(String id) {
        return commentRepository.findById(id).map(commentConverter::toDto);
    }

    @Override
    public List<CommentDto> findByBookId(String bookId) {
        return commentRepository.findByBookId(bookId).stream()
                .map(commentConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto insert(String text, String bookId) {
        return commentConverter.toDto(save(null, text, bookId));
    }

    @Override
    @Transactional
    public CommentDto update(String id, String text, String bookId) {
        return commentConverter.toDto(save(id, text, bookId));
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        commentRepository.deleteById(id);
    }

    private Comment save(String id, String text, String bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));
        var comment = new Comment(id, text, book);
        return commentRepository.save(comment);
    }
}
