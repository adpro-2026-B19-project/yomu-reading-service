package id.ac.ui.cs.advprog.yomureadingservice.reading.controller;

import id.ac.ui.cs.advprog.yomureadingservice.client.ReadingServiceClient;
import id.ac.ui.cs.advprog.yomureadingservice.client.ReadingServiceException;
import id.ac.ui.cs.advprog.yomureadingservice.client.ReadingServiceNotFoundException;
import id.ac.ui.cs.advprog.yomureadingservice.client.dto.QuizAttemptDto;
import id.ac.ui.cs.advprog.yomureadingservice.client.dto.QuizResponseDto;
import id.ac.ui.cs.advprog.yomureadingservice.client.dto.TextPageDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/texts")
public class TextApiController {

    private final ReadingServiceClient readingServiceClient;

    public TextApiController(ReadingServiceClient readingServiceClient) {
        this.readingServiceClient = readingServiceClient;
    }

    @GetMapping
    public ResponseEntity<?> getAllTexts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        try {
            TextPageDto result = readingServiceClient.getAllTexts(page, size);
            return ResponseEntity.ok(result);
        } catch (ReadingServiceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ReadingServiceException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Reading Service tidak tersedia. Silakan coba lagi nanti."));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User belum terautentikasi"));
        }

        String userId = authentication.getName();
        try {
            return ResponseEntity.ok(readingServiceClient.getReadingStats(userId));
        } catch (ReadingServiceNotFoundException e) {
            return ResponseEntity.ok(Map.of("totalTextsCompleted", 0, "averageAccuracy", 0.0, "totalAccumulatedScore", 0.0));
        } catch (ReadingServiceException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Reading Service tidak tersedia. Silakan coba lagi nanti."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTextDetail(@PathVariable Long id, Authentication authentication) {
        try {
            Map<String, Object> result = readingServiceClient.getTextById(id);
            return ResponseEntity.ok(result);
        } catch (ReadingServiceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Text tidak ditemukan"));
        } catch (ReadingServiceException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Reading Service tidak tersedia. Silakan coba lagi nanti."));
        }
    }

    @GetMapping("/{id}/quiz")
    public ResponseEntity<?> startQuiz(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User belum terautentikasi"));
        }

        try {
            QuizResponseDto quiz = readingServiceClient.getQuizByTextId(id);
            return ResponseEntity.ok(quiz);
        } catch (ReadingServiceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Text atau quiz tidak ditemukan"));
        } catch (ReadingServiceException e) {
            // Also handles already_attempted as a BAD_REQUEST from the service
            if (e.getMessage() != null && e.getMessage().contains("already_attempted")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "already_attempted"));
            }
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Reading Service tidak tersedia. Silakan coba lagi nanti."));
        }
    }

    @PostMapping("/{id}/quiz/submit")
    public ResponseEntity<?> submitQuiz(
            @PathVariable Long id,
            @RequestBody Map<String, String> answers,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User belum terautentikasi"));
        }

        try {
            QuizAttemptDto attempt = readingServiceClient.submitQuiz(id, answers);
            return ResponseEntity.ok(attempt);
        } catch (ReadingServiceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Teks bacaan tidak ditemukan atau telah dihapus"));
        } catch (ReadingServiceException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}