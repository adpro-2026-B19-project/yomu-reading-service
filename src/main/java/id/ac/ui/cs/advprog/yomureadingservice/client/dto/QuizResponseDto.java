package id.ac.ui.cs.advprog.yomureadingservice.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponseDto {
    private TextDto text;
    private List<QuestionDto> questions;
}
