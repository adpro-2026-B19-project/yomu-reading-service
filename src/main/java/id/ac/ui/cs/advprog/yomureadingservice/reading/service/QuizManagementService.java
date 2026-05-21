package id.ac.ui.cs.advprog.yomureadingservice.reading.service;

import id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt;
import java.util.List;
import java.util.Map;

public interface QuizManagementService {
    boolean hasUserAttemptedQuiz(String userId, Long textId);
    QuizAttempt getQuizResult(String userId, Long textId);
    QuizAttempt submitQuiz(Long textId, String userId, Map<String, String> formData);
    List<QuizAttempt> getUserQuizHistory(String userId);
}
