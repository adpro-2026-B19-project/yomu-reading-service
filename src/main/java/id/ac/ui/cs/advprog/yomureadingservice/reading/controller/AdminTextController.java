package id.ac.ui.cs.advprog.yomureadingservice.reading.controller;

import id.ac.ui.cs.advprog.yomureadingservice.client.ReadingServiceClient;
import id.ac.ui.cs.advprog.yomureadingservice.client.ReadingServiceException;
import id.ac.ui.cs.advprog.yomureadingservice.client.ReadingServiceNotFoundException;
import id.ac.ui.cs.advprog.yomureadingservice.client.dto.TextDto;
import id.ac.ui.cs.advprog.yomureadingservice.client.dto.TextPageDto;
import id.ac.ui.cs.advprog.yomureadingservice.reading.dto.CreateTextRequest;
import id.ac.ui.cs.advprog.yomureadingservice.reading.service.TextService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/texts")
public class AdminTextController {

    private final ReadingServiceClient readingServiceClient;
    private final TextService textService;

    public AdminTextController(ReadingServiceClient readingServiceClient, TextService textService) {
        this.readingServiceClient = readingServiceClient;
        this.textService = textService;
    }

    @PostMapping
    public ResponseEntity<?> createText(@RequestBody CreateTextRequest request, Authentication authentication) {
        try {
            TextDto createdText = readingServiceClient.createText(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdText);
        } catch (ReadingServiceException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Gagal membuat teks: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllTextsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        try {
            TextPageDto result = readingServiceClient.getAllTexts(page, size);
            return ResponseEntity.ok(result.getTexts());
        } catch (ReadingServiceException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Reading Service tidak tersedia. Silakan coba lagi nanti."));
        }
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