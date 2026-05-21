package id.ac.ui.cs.advprog.yomureadingservice.client.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextDto {
    private Long id;
    private String title;
    private String content;
    private CategoryDto category;
    private String createdByUserId;
    private boolean published;
    private Instant createdAt;
}
