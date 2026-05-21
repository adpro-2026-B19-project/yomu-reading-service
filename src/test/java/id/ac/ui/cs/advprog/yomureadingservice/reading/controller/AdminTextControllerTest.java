package id.ac.ui.cs.advprog.yomureadingservice.reading.controller;

import id.ac.ui.cs.advprog.yomureadingservice.client.ReadingServiceClient;
import id.ac.ui.cs.advprog.yomureadingservice.client.ReadingServiceException;
import id.ac.ui.cs.advprog.yomureadingservice.client.dto.TextDto;
import id.ac.ui.cs.advprog.yomureadingservice.client.dto.TextPageDto;
import id.ac.ui.cs.advprog.yomureadingservice.reading.dto.CreateTextRequest;
import id.ac.ui.cs.advprog.yomureadingservice.reading.service.TextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminTextControllerTest {

    @Mock
    private ReadingServiceClient readingServiceClient;

    @Mock
    private TextService textService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AdminTextController adminTextController;

    private CreateTextRequest request;

    @BeforeEach
    void setUp() {
        request = new CreateTextRequest();
        request.setTitle("Title");
        request.setContent("Content");
    }

    @Test
    void testCreateTextSuccess() {
        TextDto dto = new TextDto();
        dto.setId(1L);
        when(readingServiceClient.createText(any())).thenReturn(dto);

        ResponseEntity<?> response = adminTextController.createText(request, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testCreateTextException() {
        when(readingServiceClient.createText(any())).thenThrow(new ReadingServiceException("error"));

        ResponseEntity<?> response = adminTextController.createText(request, authentication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("error"));
    }

    @Test
    void testGetAllTextsForAdminSuccess() {
        TextPageDto pageDto = new TextPageDto();
        pageDto.setTexts(List.of(new TextDto()));
        when(readingServiceClient.getAllTexts(anyInt(), anyInt())).thenReturn(pageDto);

        ResponseEntity<?> response = adminTextController.getAllTextsForAdmin(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetAllTextsForAdminException() {
        when(readingServiceClient.getAllTexts(anyInt(), anyInt())).thenThrow(new ReadingServiceException("error"));

        ResponseEntity<?> response = adminTextController.getAllTextsForAdmin(0, 10);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    @Test
    void testDeleteTextSuccess() {
        doNothing().when(textService).deleteText(1L);

        ResponseEntity<?> response = adminTextController.deleteText(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDeleteTextException() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found")).when(textService).deleteText(1L);

        ResponseEntity<?> response = adminTextController.deleteText(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
