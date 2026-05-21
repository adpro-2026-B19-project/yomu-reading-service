package id.ac.ui.cs.advprog.yomureadingservice.reading.service;

import id.ac.ui.cs.advprog.yomureadingservice.integration.reading.ReadingStatsPort.UserReadingStats;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.QuizAttemptRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadingStatsAdapterTest {

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @InjectMocks
    private ReadingStatsAdapter readingStatsAdapter;

    @Test
    void testGetUserReadingStatsNullId() {
        UserReadingStats stats = readingStatsAdapter.getUserReadingStats(null);
        assertEquals(0, stats.totalTextsCompleted());
        assertEquals(0.0, stats.averageAccuracy());
        assertEquals(0.0, stats.totalScore());
    }

    @Test
    void testGetUserReadingStatsEmpty() {
        UUID uuid = UUID.randomUUID();
        when(quizAttemptRepository.findByUserId(uuid.toString())).thenReturn(List.of());
        UserReadingStats stats = readingStatsAdapter.getUserReadingStats(uuid);
        assertEquals(0, stats.totalTextsCompleted());
    }

    @Test
    void testGetUserReadingStatsNotEmpty() {
        UUID uuid = UUID.randomUUID();
        QuizAttempt a1 = new QuizAttempt();
        QuizAttempt a2 = new QuizAttempt();
        try {
            java.lang.reflect.Field accField = QuizAttempt.class.getDeclaredField("accuracy");
            accField.setAccessible(true);
            accField.set(a1, 1.0);
            accField.set(a2, 0.5);

            java.lang.reflect.Field scoreField = QuizAttempt.class.getDeclaredField("score");
            scoreField.setAccessible(true);
            scoreField.set(a1, 100.0);
            scoreField.set(a2, 50.0);
        } catch (Exception e) {}

        when(quizAttemptRepository.findByUserId(uuid.toString())).thenReturn(List.of(a1, a2));
        UserReadingStats stats = readingStatsAdapter.getUserReadingStats(uuid);
        assertEquals(2, stats.totalTextsCompleted());
        assertEquals(0.75, stats.averageAccuracy());
        assertEquals(150.0, stats.totalScore());
    }
}
