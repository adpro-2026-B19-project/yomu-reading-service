package id.ac.ui.cs.advprog.yomureadingservice.reading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReadingStatResponse {
    private int totalTextsCompleted;
    private double averageAccuracy;
    private double totalAccumulatedScore;
}
