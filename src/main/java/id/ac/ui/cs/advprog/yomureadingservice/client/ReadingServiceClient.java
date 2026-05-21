package id.ac.ui.cs.advprog.yomureadingservice.client;

import id.ac.ui.cs.advprog.yomureadingservice.client.dto.QuizAttemptDto;
import id.ac.ui.cs.advprog.yomureadingservice.client.dto.QuizResponseDto;
import id.ac.ui.cs.advprog.yomureadingservice.client.dto.TextDto;
import id.ac.ui.cs.advprog.yomureadingservice.client.dto.TextPageDto;
import id.ac.ui.cs.advprog.yomureadingservice.reading.dto.CreateTextRequest;
import id.ac.ui.cs.advprog.yomureadingservice.reading.dto.UserReadingStatResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Component
public class ReadingServiceClient {

    private final RestClient restClient;

    public ReadingServiceClient(RestClient readingServiceRestClient) {
        this.restClient = readingServiceRestClient;
    }

    public TextPageDto getAllTexts(int page, int size) {
        try {
            return restClient.get()
                    .uri("/api/texts?page={page}&size={size}", page, size)
                    .retrieve()
                    .body(TextPageDto.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ReadingServiceNotFoundException("Texts not found", e);
            }
            throw new ReadingServiceException("Failed to fetch texts: " + e.getMessage(), e);
        } catch (RestClientException e) {
            throw new ReadingServiceException("Reading Service unavailable: " + e.getMessage(), e);
        }
    }

    /**
     * Fetches the detail of a single text by ID.
     * Corresponds to: {@code GET /api/texts/{id}}
     *
     * @return raw map containing {@code text} (TextDto) and {@code hasAttempted} (boolean)
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getTextById(Long id) {
        try {
            return restClient.get()
                    .uri("/api/texts/{id}", id)
                    .retrieve()
                    .body(Map.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ReadingServiceNotFoundException("Text not found with id: " + id, e);
            }
            throw new ReadingServiceException("Failed to fetch text " + id + ": " + e.getMessage(), e);
        } catch (RestClientException e) {
            throw new ReadingServiceException("Reading Service unavailable: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a new text (admin operation).
     * Corresponds to: {@code POST /api/texts}
     */
    public TextDto createText(CreateTextRequest request) {
        try {
            return restClient.post()
                    .uri("/api/texts")
                    .body(request)
                    .retrieve()
                    .body(TextDto.class);
        } catch (HttpClientErrorException e) {
            throw new ReadingServiceException("Failed to create text: " + e.getMessage(), e);
        } catch (RestClientException e) {
            throw new ReadingServiceException("Reading Service unavailable: " + e.getMessage(), e);
        }
    }

    /**
     * Publishes an existing text.
     * Corresponds to: {@code PUT /api/texts/{id}/publish}
     */
    public void publishText(Long id) {
        try {
            restClient.put()
                    .uri("/api/texts/{id}/publish", id)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ReadingServiceNotFoundException("Text not found with id: " + id, e);
            }
            throw new ReadingServiceException("Failed to publish text " + id + ": " + e.getMessage(), e);
        } catch (RestClientException e) {
            throw new ReadingServiceException("Reading Service unavailable: " + e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------------------
    // Quiz
    // -------------------------------------------------------------------------

    /**
     * Retrieves the quiz for a given text.
     * Corresponds to: {@code GET /api/texts/{id}/quiz}
     */
    public QuizResponseDto getQuizByTextId(Long textId) {
        try {
            return restClient.get()
                    .uri("/api/texts/{id}/quiz", textId)
                    .retrieve()
                    .body(QuizResponseDto.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ReadingServiceNotFoundException("Quiz not found for text id: " + textId, e);
            }
            throw new ReadingServiceException("Failed to fetch quiz: " + e.getMessage(), e);
        } catch (RestClientException e) {
            throw new ReadingServiceException("Reading Service unavailable: " + e.getMessage(), e);
        }
    }

    /**
     * Submits quiz answers for a given text and user.
     * Corresponds to: {@code POST /api/texts/{id}/quiz/submit}
     *
     * @param textId  the text whose quiz is being submitted
     * @param answers map of {@code question_{questionId}} → selected option ID string
     */
    public QuizAttemptDto submitQuiz(Long textId, Map<String, String> answers) {
        try {
            return restClient.post()
                    .uri("/api/texts/{id}/quiz/submit", textId)
                    .body(answers)
                    .retrieve()
                    .body(QuizAttemptDto.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ReadingServiceNotFoundException("Text not found with id: " + textId, e);
            }
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ReadingServiceException("Quiz submission rejected: " + e.getMessage(), e);
            }
            throw new ReadingServiceException("Failed to submit quiz: " + e.getMessage(), e);
        } catch (RestClientException e) {
            throw new ReadingServiceException("Reading Service unavailable: " + e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------------------
    // Reading Stats
    // -------------------------------------------------------------------------

    /**
     * Retrieves reading statistics for a given user.
     * Corresponds to: {@code GET /api/reading/stats/{userId}}
     */
    public UserReadingStatResponse getReadingStats(String userId) {
        try {
            return restClient.get()
                    .uri("/api/reading/stats/{userId}", userId)
                    .retrieve()
                    .body(UserReadingStatResponse.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ReadingServiceNotFoundException("Stats not found for user: " + userId, e);
            }
            throw new ReadingServiceException("Failed to fetch reading stats: " + e.getMessage(), e);
        } catch (RestClientException e) {
            throw new ReadingServiceException("Reading Service unavailable: " + e.getMessage(), e);
        }
    }
}
