package id.ac.ui.cs.advprog.yomureadingservice.reading.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Category;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Option;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Question;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Text;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.CategoryRepository;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.OptionRepository;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.QuestionRepository;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.QuizAttemptRepository;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.TextRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class TextServiceTest {

    @Mock
    private TextRepository textRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TextService textService;

    private Text text;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category("Teknologi");
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
    void testGetAllTexts() {
        when(textRepository.findByPublishedTrue()).thenReturn(List.of(text));

        List<Text> result = textService.getAllTexts();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Judul Test", result.get(0).getTitle());
        verify(textRepository, times(1)).findByPublishedTrue();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testGetAllTextsPaged() {
        org.springframework.data.domain.Page<Text> pagedTexts = org.mockito.Mockito.mock(org.springframework.data.domain.Page.class);
        when(pagedTexts.getContent()).thenReturn(List.of(text));
        when(textRepository.findByPublishedTrue(any(org.springframework.data.domain.Pageable.class))).thenReturn(pagedTexts);

        org.springframework.data.domain.Page<Text> result = textService.getAllTexts(0, 6);

        assertFalse(result.getContent().isEmpty());
        assertEquals(1, result.getContent().size());
        assertEquals("Judul Test", result.getContent().get(0).getTitle());
        verify(textRepository, times(1)).findByPublishedTrue(any(org.springframework.data.domain.Pageable.class));
    }

    @Test
    void testGetTextByIdFound() {
        when(textRepository.findById(1L)).thenReturn(Optional.of(text));

        Text result = textService.getTextById(1L);

        assertNotNull(result);
        assertEquals("Judul Test", result.getTitle());
    }

    @Test
    void testCreateText() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(textRepository.save(any(Text.class))).thenReturn(text);

        Text result = textService.createText("Judul Baru", "Isi Baru", 1L, "user-1");

        assertNotNull(result);
        assertEquals("Judul Test", result.getTitle());
        verify(textRepository, times(1)).save(any(Text.class));
    }

    @Test
    void testHasUserAttemptedQuizTrue() {
        when(quizAttemptRepository.existsByUserIdAndTextId("user1", 1L)).thenReturn(true);
        assertTrue(textService.hasUserAttemptedQuiz("user1", 1L));
    }

    @Test
    void testSubmitQuizSuccess() {
        Question q1 = new Question();
        Option opt1 = new Option("Benar", true);
        opt1.setQuestion(q1);
        q1.setOptions(List.of(opt1));

        try {
            java.lang.reflect.Field idField = Question.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(q1, 10L);
            idField = Option.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(opt1, 100L);
        } catch (ReflectiveOperationException ignored) {
        }

        String userId = UUID.randomUUID().toString();

        when(textRepository.findByIdAndPublishedTrue(1L)).thenReturn(Optional.of(text));
        when(questionRepository.findByTextId(1L)).thenReturn(List.of(q1));
        when(optionRepository.findById(100L)).thenReturn(Optional.of(opt1));
        when(quizAttemptRepository.save(any(id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt.class)))
                .thenAnswer(invocation -> invocation.getArguments()[0]);

        java.util.Map<String, String> formData = java.util.Map.of("question_10", "100");

        id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt attempt = textService.submitQuiz(1L, userId, formData);

        assertNotNull(attempt);
        assertEquals(100.0, attempt.getScore());
        assertEquals(1.0, attempt.getAccuracy());
        verify(quizAttemptRepository, times(1)).save(any(id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt.class));
        verify(eventPublisher, times(1))
                .publishEvent(any(id.ac.ui.cs.advprog.yomureadingservice.integration.quiz.QuizCompletedEvent.class));
    }

    @Test
    void testSubmitQuizAlreadyAttempted() {
        when(quizAttemptRepository.existsByUserIdAndTextId("user-1", 1L)).thenReturn(true);

        java.util.Map<String, String> formData = java.util.Map.of("question_10", "100");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                textService.submitQuiz(1L, "user-1", formData));

        assertEquals("User has already attempted this quiz.", exception.getMessage());
    }

    @Test
    void testDeleteTextCascade() {
        textService.deleteText(1L);
        verify(quizAttemptRepository, times(1)).deleteByTextId(1L);
        verify(optionRepository, times(1)).deleteByTextId(1L);
        verify(questionRepository, times(1)).deleteByTextId(1L);
        verify(textRepository, times(1)).deleteById(1L);
    }

    @Test
    void testPublishTextRejectsQuestionWithoutEnoughOptions() {
        Question question = new Question();
        question.setOptions(List.of(new Option("A", true)));

        when(textRepository.findById(1L)).thenReturn(Optional.of(text));
        when(questionRepository.findByTextId(1L)).thenReturn(List.of(question));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> textService.publishText(1L));

        assertEquals("Each question must have at least two options.", exception.getMessage());
    }

    @Test
    void testSubmitQuizPublishesIntegrationEventWithMappedIds() {
        Question q1 = new Question();
        Option opt1 = new Option("Benar", true);
        opt1.setQuestion(q1);
        q1.setOptions(List.of(opt1));

        try {
            java.lang.reflect.Field idField = Question.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(q1, 10L);
            idField = Option.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(opt1, 100L);
        } catch (ReflectiveOperationException ignored) {
        }

        String userId = UUID.randomUUID().toString();

        when(textRepository.findByIdAndPublishedTrue(1L)).thenReturn(Optional.of(text));
        when(questionRepository.findByTextId(1L)).thenReturn(List.of(q1));
        when(optionRepository.findById(100L)).thenReturn(Optional.of(opt1));
        when(quizAttemptRepository.save(any(id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt.class)))
                .thenAnswer(invocation -> invocation.getArguments()[0]);

        textService.submitQuiz(1L, userId, java.util.Map.of("question_10", "100"));

        verify(eventPublisher).publishEvent(any(id.ac.ui.cs.advprog.yomureadingservice.integration.quiz.QuizCompletedEvent.class));
    }

    @Test
    void testGetPublishedTextByIdFound() {
        when(textRepository.findByIdAndPublishedTrue(1L)).thenReturn(Optional.of(text));
        Text result = textService.getPublishedTextById(1L);
        assertNotNull(result);
        assertEquals("Judul Test", result.getTitle());
    }

    @Test
    void testGetPublishedTextByIdNotFound() {
        when(textRepository.findByIdAndPublishedTrue(1L)).thenReturn(Optional.empty());
        org.springframework.web.server.ResponseStatusException exception = assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> textService.getPublishedTextById(1L)
        );
        assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void testGetQuizResultFound() {
        id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt attempt = new id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt();
        when(quizAttemptRepository.findByUserIdAndTextId("user1", 1L)).thenReturn(Optional.of(attempt));
        id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt result = textService.getQuizResult("user1", 1L);
        assertNotNull(result);
    }

    @Test
    void testGetQuizResultNotFound() {
        when(quizAttemptRepository.findByUserIdAndTextId("user1", 1L)).thenReturn(Optional.empty());
        id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt result = textService.getQuizResult("user1", 1L);
        org.junit.jupiter.api.Assertions.assertNull(result);
    }

    @Test
    void testSubmitQuizNoQuestions() {
        when(textRepository.findByIdAndPublishedTrue(1L)).thenReturn(Optional.of(text));
        when(questionRepository.findByTextId(1L)).thenReturn(List.of());
        
        java.util.Map<String, String> formData = new java.util.HashMap<>();
        String userId = UUID.randomUUID().toString();
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> textService.submitQuiz(1L, userId, formData));
        assertEquals("No questions available for this text.", exception.getMessage());
    }

    @Test
    void testSubmitQuizWithIncorrectOption() {
        Question q1 = new Question();
        Option opt1 = new Option("Salah", false);
        opt1.setQuestion(q1);
        q1.setOptions(List.of(opt1));

        try {
            java.lang.reflect.Field idField = Question.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(q1, 10L);
            idField = Option.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(opt1, 100L);
        } catch (ReflectiveOperationException ignored) {
        }

        when(textRepository.findByIdAndPublishedTrue(1L)).thenReturn(Optional.of(text));
        when(questionRepository.findByTextId(1L)).thenReturn(List.of(q1));
        when(optionRepository.findById(100L)).thenReturn(Optional.of(opt1));
        when(quizAttemptRepository.save(any(id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt.class)))
                .thenAnswer(invocation -> invocation.getArguments()[0]);

        java.util.Map<String, String> formData = java.util.Map.of("question_10", "100");
        String userId = UUID.randomUUID().toString();
        id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt attempt = textService.submitQuiz(1L, userId, formData);

        assertEquals(0.0, attempt.getScore());
        assertEquals(0.0, attempt.getAccuracy());
    }

    @Test
    void testGetUserReadingStatsEmpty() {
        when(quizAttemptRepository.findByUserId("user1")).thenReturn(List.of());
        id.ac.ui.cs.advprog.yomureadingservice.reading.dto.UserReadingStatResponse stats = textService.getUserReadingStats("user1");
        assertEquals(0, stats.getTotalTextsCompleted());
        assertEquals(0.0, stats.getAverageAccuracy());
        assertEquals(0.0, stats.getTotalAccumulatedScore());
    }

    @Test
    void testGetUserReadingStatsNotEmpty() {
        id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt a1 = new id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt(text, "user1", 100.0, 1.0);
        id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt a2 = new id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt(text, "user1", 50.0, 0.5);
        when(quizAttemptRepository.findByUserId("user1")).thenReturn(List.of(a1, a2));
        id.ac.ui.cs.advprog.yomureadingservice.reading.dto.UserReadingStatResponse stats = textService.getUserReadingStats("user1");
        assertEquals(2, stats.getTotalTextsCompleted());
        assertEquals(0.75, stats.getAverageAccuracy());
        assertEquals(150.0, stats.getTotalAccumulatedScore());
    }

    @Test
    void testPublishTextSuccess() {
        Question q = new Question();
        Option o1 = new Option("A", true);
        Option o2 = new Option("B", false);
        q.setOptions(List.of(o1, o2));
        when(textRepository.findById(1L)).thenReturn(Optional.of(text));
        when(questionRepository.findByTextId(1L)).thenReturn(List.of(q));
        
        textService.publishText(1L);
        assertTrue(text.isPublished());
        verify(textRepository).save(text);
    }

    @Test
    void testPublishTextNoQuestions() {
        when(textRepository.findById(1L)).thenReturn(Optional.of(text));
        when(questionRepository.findByTextId(1L)).thenReturn(List.of());
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> textService.publishText(1L));
        assertEquals("Cannot publish text without any questions.", exception.getMessage());
    }

    @Test
    void testPublishTextMultipleCorrectOptions() {
        Question q = new Question();
        Option o1 = new Option("A", true);
        Option o2 = new Option("B", true);
        q.setOptions(List.of(o1, o2));
        when(textRepository.findById(1L)).thenReturn(Optional.of(text));
        when(questionRepository.findByTextId(1L)).thenReturn(List.of(q));
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> textService.publishText(1L));
        assertEquals("Each question must have exactly one correct option.", exception.getMessage());
    }

    @Test
    void testGetAllTextsAdmin() {
        Text t2 = new Text("Unpublished", "Content", category, "user-123");
        t2.setPublished(false);
        try {
            java.lang.reflect.Field idField = Category.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(category, 1L);
        } catch (ReflectiveOperationException ignored) {}

        when(textRepository.findAll()).thenReturn(List.of(text, t2));
        
        List<Text> all = textService.getAllTextsAdmin(null, null);
        assertEquals(2, all.size());

        List<Text> pub = textService.getAllTextsAdmin(null, true);
        assertEquals(1, pub.size());
        assertEquals("Judul Test", pub.get(0).getTitle());

        List<Text> catTexts = textService.getAllTextsAdmin(1L, null);
        assertEquals(2, catTexts.size());
    }

    @Test
    void testGetUserQuizHistory() {
        id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt a1 = new id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt(text, "user1", 100.0, 1.0);
        id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt a2 = new id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt(text, "user1", 50.0, 0.5);
        try {
            java.lang.reflect.Field tsField = id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt.class.getDeclaredField("timestamp");
            tsField.setAccessible(true);
            tsField.set(a1, java.time.Instant.now().minusSeconds(100));
            tsField.set(a2, java.time.Instant.now());
        } catch (ReflectiveOperationException ignored) {}

        when(quizAttemptRepository.findByUserId("user1")).thenReturn(List.of(a1, a2));
        
        List<id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt> history = textService.getUserQuizHistory("user1");
        assertEquals(2, history.size());
        assertEquals(a2, history.get(0)); // latest first
    }
}
