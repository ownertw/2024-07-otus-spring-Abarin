package ru.otus.hw.listeners;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

@Component
public class MongoBookEventListener extends AbstractMongoEventListener<Book> {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoBookEventListener(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void onAfterDelete(AfterDeleteEvent<Book> event) {
        Document document = event.getDocument();
        String bookId = document.get("_id").toString();
        mongoTemplate.remove(Query.query(Criteria.where("bookId").is(bookId)), Comment.class);
    }
}