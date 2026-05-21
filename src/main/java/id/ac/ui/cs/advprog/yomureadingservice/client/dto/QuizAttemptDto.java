package id.ac.ui.cs.advprog.yomureadingservice.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptDto {
    private Long id;
    private Long textId;
    private String userId;
    private Double score;
    private Double accuracy;
    private Instant timestamp;
}
