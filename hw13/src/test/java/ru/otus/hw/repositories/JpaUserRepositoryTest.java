package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.User;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с пользователями")
@DataJpaTest
public class JpaUserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private TestEntityManager testEntityManager;

    @DisplayName("должен загружать пользователя по имени")
    @Test
    void shouldReturnCorrectUserByUsername() {
        var actual = repository.findByUsername("admin");
        var expectedUser = testEntityManager.find(User.class, 1);
        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(expectedUser);
    }

}
