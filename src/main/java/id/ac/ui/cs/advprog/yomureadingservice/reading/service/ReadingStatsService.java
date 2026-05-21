package id.ac.ui.cs.advprog.yomureadingservice.reading.service;

import id.ac.ui.cs.advprog.yomureadingservice.reading.dto.UserReadingStatResponse;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.QuizAttemptRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReadingStatsService implements ReadingStatsManagementService {

    private final QuizAttemptRepository quizAttemptRepository;

    public ReadingStatsService(QuizAttemptRepository quizAttemptRepository) {
        this.quizAttemptRepository = quizAttemptRepository;
    }

    @Override
    public UserReadingStatResponse getUserReadingStats(String userId) {
        List<QuizAttempt> attempts = quizAttemptRepository.findByUserId(userId);
        int totalCompleted = attempts.size();

        if (totalCompleted == 0) {
            return new UserReadingStatResponse(0, 0.0, 0.0);
        }

        double totalAccuracy = 0;
        double totalScore = 0;
        for (QuizAttempt attempt : attempts) {
            totalAccuracy += attempt.getAccuracy();
            totalScore += attempt.getScore();
        }

        double averageAccuracy = totalAccuracy / totalCompleted;
        return new UserReadingStatResponse(totalCompleted, averageAccuracy, totalScore);
    }
}
