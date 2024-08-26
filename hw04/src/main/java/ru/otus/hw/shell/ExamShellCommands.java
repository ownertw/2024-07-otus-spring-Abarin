package ru.otus.hw.shell;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent
public class ExamShellCommands {

    private final TestRunnerService testRunnerService;

    public ExamShellCommands(TestRunnerService testRunnerService) {
        this.testRunnerService = testRunnerService;
    }

    @ShellMethod(value = "Run test for student", key = {"t", "test"})
    public void runTestForStudent() {
        testRunnerService.run();
    }
}
