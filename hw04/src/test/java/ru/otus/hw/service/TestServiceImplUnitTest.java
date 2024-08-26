package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest(classes = TestServiceImpl.class)
public class TestServiceImplUnitTest {
    @MockBean
    private LocalizedIOService localizedIOService;

    @MockBean
    private QuestionDao questionDao;

    @Autowired
    private TestServiceImpl testService;

    @Test
    void executeTestForStudentShouldReturnCorrectResultTest() {
        Answer answer1 = new Answer("Red", false);
        Answer answer2 = new Answer("Yellow", true);
        Question question = new Question("What is your favorite color?", Arrays.asList(answer1, answer2));
        Student student = new Student("test", "testovich");

        when(questionDao.findAll()).thenReturn(List.of(question));
        when(localizedIOService.readIntForRangeWithPromptLocalized(anyInt(), anyInt(), anyString(), anyString())).thenReturn(2);

        TestResult result = testService.executeTestFor(student);

        assertEquals(student, result.getStudent());
        assertEquals(1, result.getAnsweredQuestions().size());
        assertEquals(1, result.getRightAnswersCount());

        verify(localizedIOService).printLine("");
        verify(localizedIOService).printLineLocalized("TestService.answer.the.questions");
        verify(localizedIOService).printLine("What is your favorite color?");
        verify(localizedIOService).printFormattedLine("%d: Red", 1);
        verify(localizedIOService).printFormattedLine("%d: Yellow", 2);

        verify(questionDao).findAll();
    }
}