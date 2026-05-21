package id.ac.ui.cs.advprog.yomureadingservice.reading.service;

import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Option;
import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Question;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.OptionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PercentageQuizScorer implements QuizScorer {
    @Override
    public QuizScore calculate(List<Question> questions, Map<String, String> answers, OptionRepository optionRepository) {
        int correctAnswers = 0;
        int totalQuestions = questions.size();

        for (Question question : questions) {
            String answerOptionIdStr = answers.get("question_" + question.getId());
            if (answerOptionIdStr != null) {
                try {
                    Long optionId = Long.parseLong(answerOptionIdStr);
                    Option selectedOption = optionRepository.findById(optionId).orElse(null);
                    if (selectedOption != null && selectedOption.isCorrect()) {
                        correctAnswers++;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        double accuracy = totalQuestions > 0 ? (double) correctAnswers / totalQuestions : 0.0;
        double score = accuracy * 100;

        return new QuizScore(correctAnswers, accuracy, score);
    }
}
