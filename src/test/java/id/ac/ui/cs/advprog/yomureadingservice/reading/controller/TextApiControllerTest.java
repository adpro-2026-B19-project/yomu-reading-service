package id.ac.ui.cs.advprog.yomureadingservice.reading.controller;

import id.ac.ui.cs.advprog.yomureadingservice.client.ReadingServiceClient;
import id.ac.ui.cs.advprog.yomureadingservice.client.ReadingServiceException;
import id.ac.ui.cs.advprog.yomureadingservice.client.ReadingServiceNotFoundException;
import id.ac.ui.cs.advprog.yomureadingservice.client.dto.QuizAttemptDto;
import id.ac.ui.cs.advprog.yomureadingservice.client.dto.QuizResponseDto;
import id.ac.ui.cs.advprog.yomureadingservice.client.dto.TextDto;
import id.ac.ui.cs.advprog.yomureadingservice.client.dto.TextPageDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TextApiControllerTest {

    @Mock
    private ReadingServiceClient readingServiceClient;

    @InjectMocks
    private TextApiController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getTexts_returns200_withPageDto() throws Exception {
        TextPageDto page = new TextPageDto(
                List.of(new TextDto(1L, "Test Title", "Content", null, "user-1", true, null)),
                0, 1, false, false
        );
        when(readingServiceClient.getAllTexts(0, 6)).thenReturn(page);

        mockMvc.perform(get("/api/texts")
                        .param("page", "0")
                        .param("size", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.texts[0].title").value("Test Title"))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void getTexts_returns503_whenServiceUnavailable() throws Exception {
        when(readingServiceClient.getAllTexts(anyInt(), anyInt()))
                .thenThrow(new ReadingServiceException("Connection refused"));

        mockMvc.perform(get("/api/texts"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getTexts_returns404_whenNotFound() throws Exception {
        when(readingServiceClient.getAllTexts(anyInt(), anyInt()))
                .thenThrow(new ReadingServiceNotFoundException("Not found"));

        mockMvc.perform(get("/api/texts"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTextDetail_returns200_whenFound() throws Exception {
        Map<String, Object> response = Map.of(
                "text", Map.of("id", 1, "title", "Title"),
                "hasAttempted", false
        );
        when(readingServiceClient.getTextById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/texts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasAttempted").value(false));
    }

    @Test
    void getTextDetail_returns404_whenNotFound() throws Exception {
        when(readingServiceClient.getTextById(99L))
                .thenThrow(new ReadingServiceNotFoundException("Text not found with id: 99"));

        mockMvc.perform(get("/api/texts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getTextDetail_returns503_whenServiceUnavailable() throws Exception {
        when(readingServiceClient.getTextById(1L))
                .thenThrow(new ReadingServiceException("Service down"));

        mockMvc.perform(get("/api/texts/1"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void startQuiz_returns401_whenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/texts/1/quiz"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void startQuiz_returns200_whenAuthenticated() throws Exception {
        QuizResponseDto quizResponse = new QuizResponseDto(
                new TextDto(1L, "Title", "Content", null, "user-1", true, null),
                List.of()
        );
        when(readingServiceClient.getQuizByTextId(1L)).thenReturn(quizResponse);

        Authentication auth = new UsernamePasswordAuthenticationToken("test-user", null, List.of());

        mockMvc.perform(get("/api/texts/1/quiz").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text.title").value("Title"));
    }

    @Test
    void startQuiz_returns404_whenNotFound() throws Exception {
        when(readingServiceClient.getQuizByTextId(99L))
                .thenThrow(new ReadingServiceNotFoundException("Quiz not found for text id: 99"));

        Authentication auth = new UsernamePasswordAuthenticationToken("test-user", null, List.of());

        mockMvc.perform(get("/api/texts/99/quiz").principal(auth))
                .andExpect(status().isNotFound());
    }

    @Test
    void startQuiz_returns400_whenAlreadyAttempted() throws Exception {
        when(readingServiceClient.getQuizByTextId(1L))
                .thenThrow(new ReadingServiceException("already_attempted"));

        Authentication auth = new UsernamePasswordAuthenticationToken("test-user", null, List.of());

        mockMvc.perform(get("/api/texts/1/quiz").principal(auth))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("already_attempted"));
    }

    @Test
    void startQuiz_returns503_whenServiceUnavailable() throws Exception {
        when(readingServiceClient.getQuizByTextId(1L))
                .thenThrow(new ReadingServiceException("Error"));

        Authentication auth = new UsernamePasswordAuthenticationToken("test-user", null, List.of());

        mockMvc.perform(get("/api/texts/1/quiz").principal(auth))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void submitQuiz_returns401_whenUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/texts/1/quiz/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question_1\": \"2\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void submitQuiz_returns200_whenSuccessful1() throws Exception {
        QuizAttemptDto attempt = new QuizAttemptDto(1L, 1L, "test-user", 80.0, 0.8, null);
        when(readingServiceClient.submitQuiz(eq(1L), any())).thenReturn(attempt);

        Authentication auth = new UsernamePasswordAuthenticationToken("test-user", null, List.of());

        mockMvc.perform(post("/api/texts/1/quiz/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question_1\": \"2\"}")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(80.0));
    }

    @Test
    void submitQuiz_returns404_whenTextNotFound() throws Exception {
        when(readingServiceClient.submitQuiz(eq(99L), any()))
                .thenThrow(new ReadingServiceNotFoundException("Text not found with id: 99"));

        Authentication auth = new UsernamePasswordAuthenticationToken("test-user", null, List.of());

        mockMvc.perform(post("/api/texts/99/quiz/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .principal(auth))
                .andExpect(status().isNotFound());
    }

    @Test
    void submitQuiz_returns400_whenServiceException() throws Exception {
        when(readingServiceClient.submitQuiz(eq(1L), any()))
                .thenThrow(new ReadingServiceException("Error"));

        Authentication auth = new UsernamePasswordAuthenticationToken("test-user", null, List.of());

        mockMvc.perform(post("/api/texts/1/quiz/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .principal(auth))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error"));
    }

    @Test
    void getHistory_returns401_whenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/texts/history"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getHistory_returns200_whenSuccessful() throws Exception {
        id.ac.ui.cs.advprog.yomureadingservice.reading.dto.UserReadingStatResponse stats = 
                new id.ac.ui.cs.advprog.yomureadingservice.reading.dto.UserReadingStatResponse(2, 0.8, 160.0);
        when(readingServiceClient.getReadingStats("test-user")).thenReturn(stats);

        Authentication auth = new UsernamePasswordAuthenticationToken("test-user", null, List.of());

        mockMvc.perform(get("/api/texts/history").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTextsCompleted").value(2));
    }

    @Test
    void getHistory_returns200_whenNotFound() throws Exception {
        when(readingServiceClient.getReadingStats("test-user"))
                .thenThrow(new ReadingServiceNotFoundException("not found"));

        Authentication auth = new UsernamePasswordAuthenticationToken("test-user", null, List.of());

        mockMvc.perform(get("/api/texts/history").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTextsCompleted").value(0));
    }

    @Test
    void getHistory_returns503_whenServiceUnavailable() throws Exception {
        when(readingServiceClient.getReadingStats("test-user"))
                .thenThrow(new ReadingServiceException("error"));

        Authentication auth = new UsernamePasswordAuthenticationToken("test-user", null, List.of());

        mockMvc.perform(get("/api/texts/history").principal(auth))
                .andExpect(status().isServiceUnavailable());
    }
}
