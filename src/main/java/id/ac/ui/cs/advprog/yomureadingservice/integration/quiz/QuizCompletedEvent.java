package id.ac.ui.cs.advprog.yomureadingservice.integration.quiz;

import java.time.LocalDateTime;
import java.util.UUID;

public record QuizCompletedEvent(
        UUID eventId,
        UUID userId,
        Long readingTextId,
        double score,
        double accuracy,
        LocalDateTime completedAt
) {
}
