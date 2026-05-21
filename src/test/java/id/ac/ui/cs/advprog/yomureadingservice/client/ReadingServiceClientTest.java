package id.ac.ui.cs.advprog.yomureadingservice.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Mock private RestClient restClient;
    @Mock private RestClient.RequestHeadersUriSpec headersUriSpec;
    @Mock private RestClient.RequestHeadersSpec headersSpec;
    @Mock private RestClient.ResponseSpec responseSpec;
    @Mock private RestClient.RequestBodyUriSpec bodyUriSpec;
    @Mock private RestClient.RequestBodySpec bodySpec;

    private ReadingServiceClient client;

    @BeforeEach
    void setUp() {
        client = new ReadingServiceClient(restClient);
    }

    private <T> void stubGet(Object returnValue, Class<T> type) {
        lenient().when(restClient.get()).thenReturn(headersUriSpec);
        lenient().when(headersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(headersSpec);
        lenient().when(headersUriSpec.uri(anyString())).thenReturn(headersSpec);
        lenient().when(headersSpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.body(type)).thenReturn((T) returnValue);
    }

    private void stubGetThrows(Throwable t) {
        lenient().when(restClient.get()).thenReturn(headersUriSpec);
        lenient().when(headersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(headersSpec);
        lenient().when(headersUriSpec.uri(anyString())).thenReturn(headersSpec);
        lenient().when(headersSpec.retrieve()).thenThrow(t);
    }

    private <T> void stubPost(Object returnValue, Class<T> type) {
        lenient().when(restClient.post()).thenReturn(bodyUriSpec);
        lenient().when(bodyUriSpec.uri(eq("/api/texts"))).thenReturn(bodySpec);
        lenient().when(bodyUriSpec.uri(eq("/api/texts/{id}/quiz/submit"), any(Object.class))).thenReturn(bodySpec);

        lenient().when(bodySpec.contentType(any(MediaType.class))).thenReturn(bodySpec);
        lenient().when(bodySpec.accept(any(MediaType.class))).thenReturn(bodySpec);

        lenient().when(bodySpec.body(any())).thenReturn(bodySpec);

        lenient().when(bodySpec.retrieve()).thenReturn(responseSpec);
        lenient().when(responseSpec.body(type)).thenReturn((T) returnValue);
    }

    private void stubPostThrows(Throwable t) {
        lenient().when(restClient.post()).thenReturn(bodyUriSpec);
        lenient().when(bodyUriSpec.uri(eq("/api/texts"))).thenReturn(bodySpec);
        lenient().when(bodyUriSpec.uri(eq("/api/texts/{id}/quiz/submit"), any(Object.class))).thenReturn(bodySpec);

        lenient().when(bodySpec.contentType(any(MediaType.class))).thenReturn(bodySpec);
        lenient().when(bodySpec.accept(any(MediaType.class))).thenReturn(bodySpec);

        lenient().when(bodySpec.body(any())).thenReturn(bodySpec);

        lenient().when(bodySpec.retrieve()).thenThrow(t);
    }

    @Test
    void getAllTexts_returnsPageDto_whenServiceRespondsOk() {
        TextPageDto expected = new TextPageDto(List.of(new TextDto()), 0, 1, false, false);
        stubGet(expected, TextPageDto.class);

        TextPageDto result = client.getAllTexts(0, 6);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getAllTexts_throwsReadingServiceException_whenServiceUnavailable() {
        stubGetThrows(new RestClientException("Connection refused"));

        assertThatThrownBy(() -> client.getAllTexts(0, 6))
                .isInstanceOf(ReadingServiceException.class)
                .hasMessageContaining("unavailable");
    }

    @Test
    void getAllTexts_throwsNotFoundException_when404() {
        stubGetThrows(HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        assertThatThrownBy(() -> client.getAllTexts(0, 6))
                .isInstanceOf(ReadingServiceNotFoundException.class);
    }

    @Test
    void getTextById_returnsMap_whenFound() {
        Map<String, Object> expected = Map.of("text", new TextDto(), "hasAttempted", false);
        stubGet(expected, Map.class);

        Map<String, Object> result = client.getTextById(1L);

        assertThat(result).containsKey("text");
    }

    @Test
    void getTextById_throwsNotFoundException_when404() {
        stubGetThrows(HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        assertThatThrownBy(() -> client.getTextById(99L))
                .isInstanceOf(ReadingServiceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getTextById_throwsReadingServiceException_whenServiceUnavailable() {
        stubGetThrows(new RestClientException("timeout"));

        assertThatThrownBy(() -> client.getTextById(1L))
                .isInstanceOf(ReadingServiceException.class);
    }

    @Test
    void createText_returnsTextDto_whenCreated() {
        TextDto expected = new TextDto(1L, "Title", "Content", null, "user-1", false, null);
        stubPost(expected, TextDto.class);

        CreateTextRequest request = new CreateTextRequest();
        request.setTitle("Title");
        TextDto result = client.createText(request);

        assertThat(result.getTitle()).isEqualTo("Title");
    }

    @Test
    void createText_throwsReadingServiceException_whenServiceUnavailable() {
        stubPostThrows(new RestClientException("timeout"));

        assertThatThrownBy(() -> client.createText(new CreateTextRequest()))
                .isInstanceOf(ReadingServiceException.class);
    }

    @Test
    void getQuizByTextId_returnsQuizResponseDto_whenFound() {
        QuizResponseDto expected = new QuizResponseDto(new TextDto(), List.of());
        stubGet(expected, QuizResponseDto.class);

        QuizResponseDto result = client.getQuizByTextId(1L);

        assertThat(result).isNotNull();
    }

    @Test
    void getQuizByTextId_throwsNotFoundException_when404() {
        stubGetThrows(HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        assertThatThrownBy(() -> client.getQuizByTextId(42L))
                .isInstanceOf(ReadingServiceNotFoundException.class);
    }

    @Test
    void submitQuiz_returnsAttemptDto_whenSuccessful() {
        QuizAttemptDto expected = new QuizAttemptDto(1L, 1L, "user-1", 80.0, 0.8, null);
        stubPost(expected, QuizAttemptDto.class);

        QuizAttemptDto result = client.submitQuiz(1L, Map.of("question_1", "2"));

        assertThat(result.getScore()).isEqualTo(80.0);
    }

    @Test
    void submitQuiz_throwsNotFoundException_when404() {
        stubPostThrows(HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        assertThatThrownBy(() -> client.submitQuiz(99L, Map.of()))
                .isInstanceOf(ReadingServiceNotFoundException.class);
    }

    @Test
    void submitQuiz_throwsReadingServiceException_whenServiceUnavailable() {
        stubPostThrows(new RestClientException("down"));

        assertThatThrownBy(() -> client.submitQuiz(1L, Map.of()))
                .isInstanceOf(ReadingServiceException.class);
    }

    @Test
    void getReadingStats_returnsStats_whenFound() {
        UserReadingStatResponse expected = new UserReadingStatResponse(5, 0.8, 400.0);
        stubGet(expected, UserReadingStatResponse.class);

        UserReadingStatResponse result = client.getReadingStats("user-1");

        assertThat(result.getTotalTextsCompleted()).isEqualTo(5);
        assertThat(result.getAverageAccuracy()).isEqualTo(0.8);
    }

    @Test
    void getReadingStats_throwsNotFoundException_when404() {
        stubGetThrows(HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        assertThatThrownBy(() -> client.getReadingStats("user-1"))
                .isInstanceOf(ReadingServiceNotFoundException.class);
    }

    @Test
    void getReadingStats_throwsReadingServiceException_whenServiceUnavailable() {
        stubGetThrows(new RestClientException("Service down"));

        assertThatThrownBy(() -> client.getReadingStats("user-1"))
                .isInstanceOf(ReadingServiceException.class)
                .hasMessageContaining("unavailable");
    }
}