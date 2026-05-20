package id.ac.ui.cs.advprog.yomureadingservice.integration.reading;

import java.util.UUID;

public interface ReadingStatsPort {
    UserReadingStats getUserReadingStats(UUID userId);

    record UserReadingStats(
            long totalTextsCompleted,
            double averageAccuracy,
            double totalScore
    ) {
    }
}
