package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.otus.hw.models.Genre;

public interface MongoGenreRepository extends ReactiveMongoRepository<Genre, String> {

}
