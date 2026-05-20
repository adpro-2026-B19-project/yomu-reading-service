package id.ac.ui.cs.advprog.yomureadingservice.reading.controller;

import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Question;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Text;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.QuestionRepository;
import id.ac.ui.cs.advprog.yomureadingservice.reading.service.TextService;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/texts")
public class TextApiController {

    private final QuestionRepository questionRepository;
    private final TextService textService;

    public TextApiController(QuestionRepository questionRepository, TextService textService) {
        this.questionRepository = questionRepository;
        this.textService = textService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTexts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        
        Page<Text> textPage = textService.getAllTexts(page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("texts", textPage.getContent());
        response.put("currentPage", page);
        response.put("totalPages", textPage.getTotalPages());
        response.put("hasNext", textPage.hasNext());
        response.put("hasPrevious", textPage.hasPrevious());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User belum terautentikasi"));
        }
        
        String userId = authentication.getName();
        List<QuizAttempt> history = textService.getUserQuizHistory(userId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTextDetail(@PathVariable Long id, Authentication authentication) {
        try {
            Text text = textService.getPublishedTextById(id);

            boolean hasAttempted = false;
            if (authentication != null && authentication.isAuthenticated()) {
                String userId = authentication.getName();
                hasAttempted = textService.hasUserAttemptedQuiz(userId, id);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("text", text);
            response.put("hasAttempted", hasAttempted);
            
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        }
    }

    @GetMapping("/{id}/quiz")
    public ResponseEntity<?> startQuiz(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User belum terautentikasi"));
        }

        String userId = authentication.getName();

        try {
            if (textService.hasUserAttemptedQuiz(userId, id)) {
                return ResponseEntity.badRequest().body(Map.of("error", "already_attempted"));
            }

            Text text = textService.getPublishedTextById(id);
            List<Question> questions = questionRepository.findByTextId(id);

            Map<String, Object> response = new HashMap<>();
            response.put("text", text);
            response.put("questions", questions);

            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        }
    }

    @PostMapping("/{id}/quiz/submit")
    public ResponseEntity<?> submitQuiz(@PathVariable Long id,
            @RequestBody Map<String, String> answers, // Menggunakan @RequestBody untuk membaca JSON
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User belum terautentikasi"));
        }

        String userId = authentication.getName();

        try {
            QuizAttempt attempt = textService.submitQuiz(id, userId, answers);
            return ResponseEntity.ok(attempt);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", "Teks bacaan tidak ditemukan atau telah dihapus"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}