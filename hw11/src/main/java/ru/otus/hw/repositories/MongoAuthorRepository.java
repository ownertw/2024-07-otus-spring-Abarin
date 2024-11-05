package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.otus.hw.models.Author;

public interface MongoAuthorRepository extends ReactiveMongoRepository<Author, String> {
}
