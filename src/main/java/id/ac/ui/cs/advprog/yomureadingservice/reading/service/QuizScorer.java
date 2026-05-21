package id.ac.ui.cs.advprog.yomureadingservice.reading.service;

import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Question;
import id.ac.ui.cs.advprog.yomureadingservice.reading.repository.OptionRepository;

import java.util.List;
import java.util.Map;

public interface QuizScorer {
    QuizScore calculate(List<Question> questions, Map<String, String> answers, OptionRepository optionRepository);

    record QuizScore(int correctAnswers, double accuracy, double score) {}
}
