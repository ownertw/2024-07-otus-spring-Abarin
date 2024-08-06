import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.TestServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServiceTest {
    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkPrintTest() {
        Answer answer1 = new Answer("Red", false);
        Answer answer2 = new Answer("Yellow", true);
        Question question = new Question("What is your favorite color?", Arrays.asList(answer1, answer2));

        when(questionDao.findAll()).thenReturn(List.of(question));

        testService.executeTest();

        verify(ioService).printLine("");
        verify(ioService).printFormattedLine("Please answer the questions below%n");
        verify(ioService).printLine("What is your favorite color?");
        verify(ioService).printFormattedLine("%d: Red", 1);
        verify(ioService).printFormattedLine("%d: Yellow", 2);

        verify(questionDao).findAll();
    }
}