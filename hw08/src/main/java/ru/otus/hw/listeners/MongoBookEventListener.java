package ru.otus.hw.listeners;

import org.bson.Document;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.MongoCommentRepository;

@Component
public class MongoBookEventListener extends AbstractMongoEventListener<Book> {

    private final MongoCommentRepository mongoCommentRepository;

    public MongoBookEventListener(MongoCommentRepository mongoCommentRepository) {
        this.mongoCommentRepository = mongoCommentRepository;
    }

    @Override
    public void onAfterDelete(AfterDeleteEvent<Book> event) {
        Document document = event.getDocument();
        var bookId = document.get("_id").toString();
        var commentsByBookId = mongoCommentRepository.findByBookId(bookId);
        mongoCommentRepository.deleteAll(commentsByBookId);
    }
}