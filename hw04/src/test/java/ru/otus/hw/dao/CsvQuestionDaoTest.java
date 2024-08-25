package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {CsvQuestionDao.class})
public class CsvQuestionDaoTest {
    @MockBean
    private TestFileNameProvider testFileNameProvider;

    @Autowired
    private CsvQuestionDao csvQuestionDao;

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
