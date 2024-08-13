package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question : questions) {
            ioService.printLine(question.text());
            printAnswerForQuestion(question);
            var isAnswerValid = choiceOfAnswerOption(question.answers());
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private boolean choiceOfAnswerOption(List<Answer> answers) {
        int userOption = ioService.readIntForRangeWithPrompt(0,
                answers.size(),
                "Choose an option: ",
                "There is no such option");
        return answers.get(userOption - 1).isCorrect();
    }

    private void printAnswerForQuestion(Question question) {
        List<Answer> answers = question.answers();
        for (int i = 0; i < answers.size(); i++) {
            String answerText = answers.get(i).text();
            ioService.printFormattedLine("%d: " + answerText, i + 1);
        }
    }
}
