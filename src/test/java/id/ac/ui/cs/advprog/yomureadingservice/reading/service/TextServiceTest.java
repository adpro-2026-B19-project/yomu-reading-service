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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
