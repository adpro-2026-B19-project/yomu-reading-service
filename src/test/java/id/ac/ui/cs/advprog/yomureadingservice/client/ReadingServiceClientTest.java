package id.ac.ui.cs.advprog.yomureadingservice.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import id.ac.ui.cs.advprog.yomureadingservice.client.dto.QuizAttemptDto;
import id.ac.ui.cs.advprog.yomureadingservice.client.dto.QuizResponseDto;
import id.ac.ui.cs.advprog.yomureadingservice.client.dto.TextDto;
import id.ac.ui.cs.advprog.yomureadingservice.client.dto.TextPageDto;
import id.ac.ui.cs.advprog.yomureadingservice.reading.dto.CreateTextRequest;
import id.ac.ui.cs.advprog.yomureadingservice.reading.dto.UserReadingStatResponse;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class ReadingServiceClientTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient restClient;

    private ReadingServiceClient client;

    @BeforeEach
    void setUp() {
        client = new ReadingServiceClient(restClient);
    }

    @Test
    void getAllTexts_returnsPageDto_whenServiceRespondsOk() {
        TextPageDto expected = new TextPageDto(List.of(new TextDto()), 0, 1, false, false);
        when(restClient.get()
                .uri(eq("/api/texts?page={page}&size={size}"), eq(0), eq(6))
                .retrieve()
                .body(TextPageDto.class))
                .thenReturn(expected);

        TextPageDto result = client.getAllTexts(0, 6);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getAllTexts_throwsReadingServiceException_whenServiceUnavailable() {
        when(restClient.get()
                .uri(eq("/api/texts?page={page}&size={size}"), eq(0), eq(6))
                .retrieve()
                .body(TextPageDto.class))
                .thenThrow(new RestClientException("Connection refused"));

        assertThatThrownBy(() -> client.getAllTexts(0, 6))
                .isInstanceOf(ReadingServiceException.class)
                .hasMessageContaining("unavailable");
    }

    @Test
    void getAllTexts_throwsNotFoundException_when404() {
        when(restClient.get()
                .uri(eq("/api/texts?page={page}&size={size}"), eq(0), eq(6))
                .retrieve()
                .body(TextPageDto.class))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        assertThatThrownBy(() -> client.getAllTexts(0, 6))
                .isInstanceOf(ReadingServiceNotFoundException.class);
    }

    @Test
    void getTextById_returnsMap_whenFound() {
        Map<String, Object> expected = Map.of("text", new TextDto(), "hasAttempted", false);
        when(restClient.get()
                .uri(eq("/api/texts/{id}"), eq(1L))
                .retrieve()
                .body(Map.class))
                .thenReturn(expected);

        Map<String, Object> result = client.getTextById(1L);

        assertThat(result).containsKey("text");
    }

    @Test
    void getTextById_throwsNotFoundException_when404() {
        when(restClient.get()
                .uri(eq("/api/texts/{id}"), eq(99L))
                .retrieve()
                .body(Map.class))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        assertThatThrownBy(() -> client.getTextById(99L))
                .isInstanceOf(ReadingServiceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getTextById_throwsReadingServiceException_whenServiceUnavailable() {
        when(restClient.get()
                .uri(eq("/api/texts/{id}"), eq(1L))
                .retrieve()
                .body(Map.class))
                .thenThrow(new RestClientException("timeout"));

        assertThatThrownBy(() -> client.getTextById(1L))
                .isInstanceOf(ReadingServiceException.class);
    }

    @Test
    void createText_returnsTextDto_whenCreated() {
        TextDto expected = new TextDto(1L, "Title", "Content", null, "user-1", false, null);
        CreateTextRequest request = new CreateTextRequest();
        request.setTitle("Title");

        when(restClient.post()
                .uri(eq("/api/texts"))
                .body(eq(request))
                .retrieve()
                .body(TextDto.class))
                .thenReturn(expected);

        TextDto result = client.createText(request);

        assertThat(result.getTitle()).isEqualTo("Title");
    }

    @Test
    void createText_throwsReadingServiceException_whenServiceUnavailable() {
        CreateTextRequest request = new CreateTextRequest();
        when(restClient.post()
                .uri(eq("/api/texts"))
                .body(eq(request))
                .retrieve()
                .body(TextDto.class))
                .thenThrow(new RestClientException("timeout"));

        assertThatThrownBy(() -> client.createText(request))
                .isInstanceOf(ReadingServiceException.class);
    }

    @Test
    void getQuizByTextId_returnsQuizResponseDto_whenFound() {
        QuizResponseDto expected = new QuizResponseDto(new TextDto(), List.of());
        when(restClient.get()
                .uri(eq("/api/texts/{id}/quiz"), eq(1L))
                .retrieve()
                .body(QuizResponseDto.class))
                .thenReturn(expected);

        QuizResponseDto result = client.getQuizByTextId(1L);

        assertThat(result).isNotNull();
    }

    @Test
    void getQuizByTextId_throwsNotFoundException_when404() {
        when(restClient.get()
                .uri(eq("/api/texts/{id}/quiz"), eq(42L))
                .retrieve()
                .body(QuizResponseDto.class))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        assertThatThrownBy(() -> client.getQuizByTextId(42L))
                .isInstanceOf(ReadingServiceNotFoundException.class);
    }

    @Test
    void submitQuiz_returnsAttemptDto_whenSuccessful() {
        QuizAttemptDto expected = new QuizAttemptDto(1L, 1L, "user-1", 80.0, 0.8, null);
        Map<String, String> answers = Map.of("question_1", "2");

        when(restClient.post()
                .uri(eq("/api/texts/{id}/quiz/submit"), eq(1L))
                .body(eq(answers))
                .retrieve()
                .body(QuizAttemptDto.class))
                .thenReturn(expected);

        QuizAttemptDto result = client.submitQuiz(1L, answers);

        assertThat(result.getScore()).isEqualTo(80.0);
    }

    @Test
    void submitQuiz_throwsNotFoundException_when404() {
        Map<String, String> answers = Map.of();
        when(restClient.post()
                .uri(eq("/api/texts/{id}/quiz/submit"), eq(99L))
                .body(eq(answers))
                .retrieve()
                .body(QuizAttemptDto.class))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        assertThatThrownBy(() -> client.submitQuiz(99L, answers))
                .isInstanceOf(ReadingServiceNotFoundException.class);
    }

    @Test
    void submitQuiz_throwsReadingServiceException_whenServiceUnavailable() {
        Map<String, String> answers = Map.of();
        when(restClient.post()
                .uri(eq("/api/texts/{id}/quiz/submit"), eq(1L))
                .body(eq(answers))
                .retrieve()
                .body(QuizAttemptDto.class))
                .thenThrow(new RestClientException("down"));

        assertThatThrownBy(() -> client.submitQuiz(1L, answers))
                .isInstanceOf(ReadingServiceException.class);
    }

    @Test
    void getReadingStats_returnsStats_whenFound() {
        UserReadingStatResponse expected = new UserReadingStatResponse(5, 0.8, 400.0);
        when(restClient.get()
                .uri(eq("/api/reading/stats/{userId}"), eq("user-1"))
                .retrieve()
                .body(UserReadingStatResponse.class))
                .thenReturn(expected);

        UserReadingStatResponse result = client.getReadingStats("user-1");

        assertThat(result.getTotalTextsCompleted()).isEqualTo(5);
        assertThat(result.getAverageAccuracy()).isEqualTo(0.8);
    }

    @Test
    void getReadingStats_throwsNotFoundException_when404() {
        when(restClient.get()
                .uri(eq("/api/reading/stats/{userId}"), eq("user-1"))
                .retrieve()
                .body(UserReadingStatResponse.class))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        assertThatThrownBy(() -> client.getReadingStats("user-1"))
                .isInstanceOf(ReadingServiceNotFoundException.class);
    }

    @Test
    void getReadingStats_throwsReadingServiceException_whenServiceUnavailable() {
        when(restClient.get()
                .uri(eq("/api/reading/stats/{userId}"), eq("user-1"))
                .retrieve()
                .body(UserReadingStatResponse.class))
                .thenThrow(new RestClientException("Service down"));

        assertThatThrownBy(() -> client.getReadingStats("user-1"))
                .isInstanceOf(ReadingServiceException.class)
                .hasMessageContaining("unavailable");
    }
}