package ru.otus.hw.controllers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.hw.configuration.RoleBasedSecurityConfiguration;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import(RoleBasedSecurityConfiguration.class)
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
                                           String urlRedirectPattern,
                                           String role) throws Exception {

        var request = method2RequestBuilder(method, uri);

        if (nonNull(username)) {
            request.with(user(username).roles(role));
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
                                                    boolean redirect,
                                                    String urlRedirectPattern,
                                                    String role) throws Exception {
        var request = method2RequestBuilder(method, uri);

        if (nonNull(username)) {
            request.with(user(username).roles(role));
        }

        ResultActions resultActions = mockMvc.perform(request.param("bookId", bookId))
                .andExpect(status().is(status));
        if (redirect) {
            resultActions.andExpect(redirectedUrlPattern(urlRedirectPattern));
        }

    }

    private MockHttpServletRequestBuilder method2RequestBuilder(String method, String url) {
        Map<String, Function<String, MockHttpServletRequestBuilder>> methodMap =
                Map.of("get", MockMvcRequestBuilders::get,
                        "post", MockMvcRequestBuilders::post);
        return methodMap.get(method).apply(url);
    }

    private static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of("get", "/books/", null, 302, true, "**/login", null),
                Arguments.of("get", "/books/", "admin", 200, false, null, "ADMIN"),
                Arguments.of("get", "/books/", "author", 200, false, null, "AUTHOR"),
                Arguments.of("get", "/books/", "user", 200, false, null, "USER"),
                Arguments.of("get", "/books/1", null, 302, true, "**/login", null),
                Arguments.of("get", "/books/add-form", null, 302, true, "**/login", null),
                Arguments.of("get", "/books/add-form", "admin", 200, false, null, "ADMIN"),
                Arguments.of("get", "/books/add-form", "author", 200, false, null, "AUTHOR"),
                Arguments.of("get", "/books/add-form", "user", 403, false, null, "USER"),
                Arguments.of("post", "/books/update", null, 302, true, "**/login", null),
                Arguments.of("post", "/books/update", "admin", 302, true, "/books/**", "ADMIN"),
                Arguments.of("post", "/books/update", "author", 302, true, "/books/**", "AUTHOR"),
                Arguments.of("post", "/books/update", "user", 403, false, null, "USER"),
                Arguments.of("post", "/books/save", null, 302, true, "**/login", null),
                Arguments.of("post", "/books/save", "admin", 302, true, "/books/**", "ADMIN"),
                Arguments.of("post", "/books/save", "author", 302, true, "/books/**", "AUTHOR"),
                Arguments.of("post", "/books/save", "user", 403, false, null, "USER")
        );
    }

    private static Stream<Arguments> getTestDeleteData() {
        return Stream.of(
                Arguments.of("post", "/books/delete", "1", null, 302, true, "**/login", null),
                Arguments.of("post", "/books/delete", "1", "admin", 302, true, "/books/**", "ADMIN"),
                Arguments.of("post", "/books/delete", "1", "author", 403, false, null, "AUTHOR"),
                Arguments.of("post", "/books/delete", "1", "user", 403, false, null, "USER")
        );
    }
}
