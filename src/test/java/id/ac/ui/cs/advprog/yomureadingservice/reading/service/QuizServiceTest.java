package id.ac.ui.cs.advprog.yomureadingservice.reading.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Category;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Option;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Question;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Text;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.OptionRepository;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.QuestionRepository;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.QuizAttemptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import id.ac.ui.cs.advprog.yomureadingservice.integration.quiz.QuizCompletedEvent;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private TextManagementService textManagementService;

    @Mock
    private QuizScorer quizScorer;

    @InjectMocks
    private QuizService quizService;

    private Text text;

    @BeforeEach
    void setUp() {
        Category category = new Category("Teknologi");
        text = new Text("Judul Test", "Konten Test", category, "user-123");
        text.setPublished(true);
        try {
            java.lang.reflect.Field idField = Text.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(text, 1L);
        } catch (ReflectiveOperationException ignored) {
        }
    }

    @Test
    void testHasUserAttemptedQuizTrue() {
        when(quizAttemptRepository.existsByUserIdAndTextId("user1", 1L)).thenReturn(true);
        assertTrue(quizService.hasUserAttemptedQuiz("user1", 1L));
    }

    @Test
    void testGetQuizResultFound() {
        QuizAttempt attempt = new QuizAttempt();
        when(quizAttemptRepository.findByUserIdAndTextId("user1", 1L)).thenReturn(Optional.of(attempt));
        QuizAttempt result = quizService.getQuizResult("user1", 1L);
        assertNotNull(result);
    }

    @Test
    void testGetQuizResultNotFound() {
        when(quizAttemptRepository.findByUserIdAndTextId("user1", 1L)).thenReturn(Optional.empty());
        QuizAttempt result = quizService.getQuizResult("user1", 1L);
        assertNull(result);
    }

    @Test
    void testSubmitQuizSuccess() {
        Question q1 = new Question();
        try {
            java.lang.reflect.Field idField = Question.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(q1, 10L);
        } catch (ReflectiveOperationException ignored) {}

        String userId = UUID.randomUUID().toString();
        Map<String, String> formData = Map.of("question_10", "100");

        when(quizAttemptRepository.existsByUserIdAndTextId(userId, 1L)).thenReturn(false);
        when(textManagementService.getPublishedTextById(1L)).thenReturn(text);
        when(questionRepository.findByTextId(1L)).thenReturn(List.of(q1));
        
        QuizScorer.QuizScore score = new QuizScorer.QuizScore(1, 1.0, 100.0);
        when(quizScorer.calculate(List.of(q1), formData, optionRepository)).thenReturn(score);

        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenAnswer(i -> i.getArguments()[0]);

        QuizAttempt attempt = quizService.submitQuiz(1L, userId, formData);

        assertNotNull(attempt);
        assertEquals(100.0, attempt.getScore());
        assertEquals(1.0, attempt.getAccuracy());
        verify(quizAttemptRepository).save(any(QuizAttempt.class));
        verify(eventPublisher).publishEvent(any(QuizCompletedEvent.class));
    }

    @Test
    void testSubmitQuizAlreadyAttempted() {
        when(quizAttemptRepository.existsByUserIdAndTextId("user-1", 1L)).thenReturn(true);
        Map<String, String> formData = Map.of("question_10", "100");
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                quizService.submitQuiz(1L, "user-1", formData));
        assertEquals("User has already attempted this quiz.", exception.getMessage());
    }

    @Test
    void testSubmitQuizNoQuestions() {
        String userId = UUID.randomUUID().toString();
        when(quizAttemptRepository.existsByUserIdAndTextId(userId, 1L)).thenReturn(false);
        when(textManagementService.getPublishedTextById(1L)).thenReturn(text);
        when(questionRepository.findByTextId(1L)).thenReturn(List.of());
        
        Map<String, String> formData = Map.of();
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> quizService.submitQuiz(1L, userId, formData));
        assertEquals("No questions available for this text.", exception.getMessage());
    }

    @Test
    void testGetUserQuizHistory() {
        QuizAttempt a1 = new QuizAttempt(text, "user1", 100.0, 1.0);
        QuizAttempt a2 = new QuizAttempt(text, "user1", 50.0, 0.5);
        try {
            java.lang.reflect.Field tsField = QuizAttempt.class.getDeclaredField("timestamp");
            tsField.setAccessible(true);
            tsField.set(a1, Instant.now().minusSeconds(100));
            tsField.set(a2, Instant.now());
        } catch (ReflectiveOperationException ignored) {}

        when(quizAttemptRepository.findByUserId("user1")).thenReturn(List.of(a1, a2));
        
        List<QuizAttempt> history = quizService.getUserQuizHistory("user1");
        assertEquals(2, history.size());
        assertEquals(a2, history.get(0)); // latest first
    }
}
