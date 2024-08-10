package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileNameProvider.getTestFileName());
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {

            CsvToBean<QuestionDto> csvToBean = new CsvToBeanBuilder<QuestionDto>(inputStreamReader)
                    .withSkipLines(1)
                    .withSeparator(';')
                    .withType(QuestionDto.class)
                    .build();

            return csvToBean.parse()
                    .stream()
                    .map(QuestionDto::toDomainObject)
                    .toList();
        } catch (Exception e) {
            throw new QuestionReadException("Error reading the questions from file: " +
                    fileNameProvider.getTestFileName(), e);
        }
    }
}
