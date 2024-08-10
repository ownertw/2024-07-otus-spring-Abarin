package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        questionDao.findAll().forEach(question -> {
            ioService.printLine(question.text());
            List<Answer> answers = question.answers();
            for (int i = 0; i < answers.size(); i++) {
                String answerText = answers.get(i).text();
                ioService.printFormattedLine("%d: " + answerText, i + 1);
            }
        });
    }
}
