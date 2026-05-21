package id.ac.ui.cs.advprog.yomureadingservice.reading.controller;

import id.ac.ui.cs.advprog.yomureadingservice.client.ReadingServiceClient;
import id.ac.ui.cs.advprog.yomureadingservice.client.ReadingServiceException;
import id.ac.ui.cs.advprog.yomureadingservice.client.ReadingServiceNotFoundException;
import id.ac.ui.cs.advprog.yomureadingservice.reading.dto.UserReadingStatResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/reading/stats")
public class ReadingStatsController {

    private final ReadingServiceClient readingServiceClient;

    public ReadingStatsController(ReadingServiceClient readingServiceClient) {
        this.readingServiceClient = readingServiceClient;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getReadingStats(@PathVariable String userId) {
        try {
            UserReadingStatResponse stats = readingServiceClient.getReadingStats(userId);
            return ResponseEntity.ok(stats);
        } catch (ReadingServiceNotFoundException e) {
            return ResponseEntity.ok(new UserReadingStatResponse(0, 0.0, 0.0));
        } catch (ReadingServiceException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Reading Service tidak tersedia. Silakan coba lagi nanti."));
        }
    }
}
