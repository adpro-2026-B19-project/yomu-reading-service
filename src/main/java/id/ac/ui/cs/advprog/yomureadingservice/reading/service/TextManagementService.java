package id.ac.ui.cs.advprog.yomureadingservice.reading.service;

import id.ac.ui.cs.advprog.yomureadingservice.reading.model.Text;
import org.springframework.data.domain.Page;
import java.util.List;

public interface TextManagementService {
    List<Text> getAllTexts();
    Page<Text> getAllTexts(int page, int size);
    Text getTextById(Long id);
    Text getPublishedTextById(Long id);
    Text createText(String title, String content, Long categoryId, String userId);
    void deleteText(Long textId);
    void publishText(Long textId);
    List<Text> getAllTextsAdmin(Long categoryId, Boolean published);
}
