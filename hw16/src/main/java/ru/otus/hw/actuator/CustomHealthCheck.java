package ru.otus.hw.actuator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.AuthorRepository;

@Component
@RequiredArgsConstructor
public class CustomHealthCheck implements HealthIndicator {

    private final AuthorRepository authorRepository;

    @Override
    public Health health() {
        if (authorRepository.count() != 0) {
            return Health.up().withDetail("message", "Авторы не пусты, можно работать").build();
        }
        return Health.down()
                .status(Status.DOWN)
                .withDetail("message", "Караул, авторы пустые!")
                .build();
    }
}
