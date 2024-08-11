import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class CsvQuestionDaoTest {
    @Mock
    private TestFileNameProvider testFileNameProvider;

    @InjectMocks
    private CsvQuestionDao csvQuestionDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findAllShouldReturnCorrectResultTest() {
        Answer answer1 = new Answer("test", false);
        Answer answer2 = new Answer("noTest", false);
        Answer answer3 = new Answer("ALLTEST!", true);
        Question question = new Question("Test?", Arrays.asList(answer1, answer2, answer3));
        Question questionTwo = new Question("Answer?", Arrays.asList(answer1, answer2, answer3));
        List<Question> expectedQuestions = new ArrayList<>(Arrays.asList(question, questionTwo));

        when(testFileNameProvider.getTestFileName()).thenReturn("test.csv");
        List<Question> questionsFromDao = csvQuestionDao.findAll();

        assertEquals(expectedQuestions,
                questionsFromDao,
                "The list of questions does not match the expected one.");
    }

    @Test
    public void findAllShouldReturnExceptionByIncorrectCsvTest() {
        when(testFileNameProvider.getTestFileName()).thenReturn("incorrect.csv");

        QuestionReadException questionReadException = assertThrows(QuestionReadException.class,
                () -> csvQuestionDao.findAll(),
                "Exception not equals!");
        assertTrue(questionReadException.getMessage().contains("Error reading the questions from file:"),
                "Not contains exception message");
    }
}
