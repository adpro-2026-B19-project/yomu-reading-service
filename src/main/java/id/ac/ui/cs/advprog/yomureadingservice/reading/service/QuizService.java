package id.ac.ui.cs.advprog.yomureadingservice.reading.service;

import id.ac.ui.cs.advprog.yomureadingservice.integration.quiz.QuizCompletedEvent;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Question;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Text;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.OptionRepository;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.QuestionRepository;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.QuizAttemptRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class QuizService implements QuizManagementService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final TextManagementService textManagementService;
    private final QuizScorer quizScorer;

    public QuizService(QuizAttemptRepository quizAttemptRepository,
                       QuestionRepository questionRepository,
                       OptionRepository optionRepository,
                       ApplicationEventPublisher eventPublisher,
                       TextManagementService textManagementService,
                       QuizScorer quizScorer) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.eventPublisher = eventPublisher;
        this.textManagementService = textManagementService;
        this.quizScorer = quizScorer;
    }

    @Override
    public boolean hasUserAttemptedQuiz(String userId, Long textId) {
        return quizAttemptRepository.existsByUserIdAndTextId(userId, textId);
    }

    @Override
    public QuizAttempt getQuizResult(String userId, Long textId) {
        return quizAttemptRepository.findByUserIdAndTextId(userId, textId).orElse(null);
    }

    @Override
    public QuizAttempt submitQuiz(Long textId, String userId, Map<String, String> formData) {
        if (hasUserAttemptedQuiz(userId, textId)) {
            throw new IllegalStateException("User has already attempted this quiz.");
        }

        Text text = textManagementService.getPublishedTextById(textId);
        List<Question> questions = questionRepository.findByTextId(textId);

        if (questions.isEmpty()) {
            throw new IllegalStateException("No questions available for this text.");
        }

        QuizScorer.QuizScore quizScore = quizScorer.calculate(questions, formData, optionRepository);

        QuizAttempt attempt = new QuizAttempt(text, userId, quizScore.score(), quizScore.accuracy());
        quizAttemptRepository.save(attempt);

        QuizCompletedEvent event = new QuizCompletedEvent(
                UUID.randomUUID(),
                UUID.fromString(userId),
                textId,
                quizScore.score(),
                quizScore.accuracy(),
                LocalDateTime.ofInstant(attempt.getTimestamp(), java.time.ZoneOffset.UTC)
        );
        eventPublisher.publishEvent(event);

        return attempt;
    }

    @Override
    public List<QuizAttempt> getUserQuizHistory(String userId) {
        List<QuizAttempt> attempts = new ArrayList<>(quizAttemptRepository.findByUserId(userId));
        attempts.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp())); // Sort descending
        return attempts;
    }
}
