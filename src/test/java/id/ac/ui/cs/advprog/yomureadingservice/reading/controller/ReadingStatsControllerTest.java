package id.ac.ui.cs.advprog.yomureadingservice.reading.controller;

import id.ac.ui.cs.advprog.yomureadingservice.client.ReadingServiceClient;
import id.ac.ui.cs.advprog.yomureadingservice.client.ReadingServiceException;
import id.ac.ui.cs.advprog.yomureadingservice.client.ReadingServiceNotFoundException;
import id.ac.ui.cs.advprog.yomureadingservice.reading.dto.UserReadingStatResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadingStatsControllerTest {

    @Mock
    private ReadingServiceClient readingServiceClient;

    @InjectMocks
    private ReadingStatsController readingStatsController;

    @Test
    void testGetReadingStatsSuccess() {
        UserReadingStatResponse stats = new UserReadingStatResponse(1, 1.0, 100.0);
        when(readingServiceClient.getReadingStats("user1")).thenReturn(stats);

        ResponseEntity<?> response = readingStatsController.getReadingStats("user1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(stats, response.getBody());
    }

    @Test
    void testGetReadingStatsNotFound() {
        when(readingServiceClient.getReadingStats(anyString())).thenThrow(new ReadingServiceNotFoundException("not found"));

        ResponseEntity<?> response = readingStatsController.getReadingStats("user1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserReadingStatResponse body = (UserReadingStatResponse) response.getBody();
        assertEquals(0, body.getTotalTextsCompleted());
    }

    @Test
    void testGetReadingStatsServiceException() {
        when(readingServiceClient.getReadingStats(anyString())).thenThrow(new ReadingServiceException("error"));

        ResponseEntity<?> response = readingStatsController.getReadingStats("user1");

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }
}
