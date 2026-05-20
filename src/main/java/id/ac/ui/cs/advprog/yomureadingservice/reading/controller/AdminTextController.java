package id.ac.ui.cs.advprog.yomureadingservice.reading.controller;

import id.ac.ui.cs.advprog.yomureadingservice.reading.dto.CreateTextRequest;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Text;
import id.ac.ui.cs.advprog.yomureadingservice.reading.service.TextService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/texts")
public class AdminTextController {

    private final TextService textService;

    public AdminTextController(TextService textService) {
        this.textService = textService;
    }

    @PostMapping
    public ResponseEntity<?> createText(@RequestBody CreateTextRequest request, Authentication authentication) {
        try {
            String authorId = (authentication != null && authentication.isAuthenticated()) 
                              ? authentication.getName() 
                              : "ADMIN";

            Text createdText = textService.createText(
                request.getTitle(),
                request.getContent(),
                request.getCategoryId(),
                authorId
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(createdText);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Gagal membuat teks: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Text>> getAllTextsForAdmin() {
        List<Text> texts = textService.getAllTexts();
        return ResponseEntity.ok(texts);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteText(@PathVariable Long id) {
        try {
            textService.deleteText(id);
            return ResponseEntity.ok(Map.of("message", "Teks dengan ID " + id + " berhasil dihapus"));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        }
    }
}