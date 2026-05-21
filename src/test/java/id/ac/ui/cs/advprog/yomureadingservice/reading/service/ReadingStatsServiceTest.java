package id.ac.ui.cs.advprog.yomureadingservice.reading.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.yomureadingservice.reading.dto.UserReadingStatResponse;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Category;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Text;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.QuizAttemptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ReadingStatsServiceTest {

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @InjectMocks
    private ReadingStatsService readingStatsService;

    private Text text;

    @BeforeEach
    void setUp() {
        Category category = new Category("Teknologi");
        text = new Text("Judul Test", "Konten Test", category, "user-123");
    }

    @Test
    void testGetUserReadingStatsEmpty() {
        when(quizAttemptRepository.findByUserId("user1")).thenReturn(List.of());
        UserReadingStatResponse stats = readingStatsService.getUserReadingStats("user1");
        assertEquals(0, stats.getTotalTextsCompleted());
        assertEquals(0.0, stats.getAverageAccuracy());
        assertEquals(0.0, stats.getTotalAccumulatedScore());
    }

    @Test
    void testGetUserReadingStatsNotEmpty() {
        QuizAttempt a1 = new QuizAttempt(text, "user1", 100.0, 1.0);
        QuizAttempt a2 = new QuizAttempt(text, "user1", 50.0, 0.5);
        when(quizAttemptRepository.findByUserId("user1")).thenReturn(List.of(a1, a2));
        
        UserReadingStatResponse stats = readingStatsService.getUserReadingStats("user1");
        
        assertEquals(2, stats.getTotalTextsCompleted());
        assertEquals(0.75, stats.getAverageAccuracy());
        assertEquals(150.0, stats.getTotalAccumulatedScore());
    }
}
