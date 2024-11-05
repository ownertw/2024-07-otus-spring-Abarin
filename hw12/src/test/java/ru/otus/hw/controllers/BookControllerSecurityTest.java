package ru.otus.hw.controllers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.hw.configuration.SimpleSecurityConfiguration;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import(SimpleSecurityConfiguration.class)
public class BookControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @ParameterizedTest(name = "{0} {1} for user {2} should return {4} status")
    @MethodSource("getTestData")
    public void shouldReturnExpectedStatus(String method,
                                           String uri,
                                           String username,
                                           int status,
                                           boolean checkLoginRedirection,
                                           String urlRedirectPattern) throws Exception {

        var request = method2RequestBuilder(method, uri);

        if (nonNull(username)) {
            request.with(user(username));
        }

        var resultActions = mockMvc.perform(request).andExpect(status().is(status));

        if (checkLoginRedirection) {
            resultActions.andExpect(redirectedUrlPattern(urlRedirectPattern));
        }
    }

    @ParameterizedTest(name = "{0} {1} for user {2} should return {4} status")
    @MethodSource("getTestDeleteData")
    public void shouldDeleteAndReturnExpectedStatus(String method,
                                                    String uri,
                                                    String bookId,
                                                    String username,
                                                    int status,
                                                    String urlRedirectPattern) throws Exception {
        var request = method2RequestBuilder(method, uri);

        if (nonNull(username)) {
            request.with(user(username));
        }

        mockMvc.perform(request.param("bookId", bookId))
                .andExpect(status().is(status))
                .andExpect(redirectedUrlPattern(urlRedirectPattern));
    }


    private MockHttpServletRequestBuilder method2RequestBuilder(String method, String url) {
        Map<String, Function<String, MockHttpServletRequestBuilder>> methodMap =
                Map.of("get", MockMvcRequestBuilders::get,
                        "post", MockMvcRequestBuilders::post);
        return methodMap.get(method).apply(url);
    }

    private static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of("get", "/books/", null, 302, true, "**/login"),
                Arguments.of("get", "/books/", "admin", 200, false, null),
                Arguments.of("get", "/books/1", null, 302, true, "**/login"),
                Arguments.of("get", "/books/add-form", null, 302, true, "**/login"),
                Arguments.of("get", "/books/add-form", "admin", 200, false, null),
                Arguments.of("post", "/books/update", null, 302, true, "**/login"),
                Arguments.of("post", "/books/update", "admin", 302, true, "/books/**"),
                Arguments.of("post", "/books/save", null, 302, true, "**/login"),
                Arguments.of("post", "/books/save", "admin", 302, true, "/books/**")
        );
    }

    private static Stream<Arguments> getTestDeleteData() {
        return Stream.of(
                Arguments.of("post", "/books/delete", "1", null, 302, "**/login"),
                Arguments.of("post", "/books/delete", "1", "admin", 302, "/books/**")
        );
    }
}
