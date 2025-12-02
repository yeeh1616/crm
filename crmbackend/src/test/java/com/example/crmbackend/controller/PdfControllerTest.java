package com.example.crmbackend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.crmbackend.service.PdfService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class PdfControllerTest {

    @Mock
    private PdfService pdfService;

    @InjectMocks
    private PdfController pdfController;

    @Test
    void getPdfReturnsPdfBytes() {
        byte[] pdfBytes = "PDF content".getBytes();

        when(pdfService.getPdfBytes(1L)).thenReturn(pdfBytes);

        ResponseEntity<byte[]> result = pdfController.getPdf(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(pdfBytes);
        assertThat(result.getHeaders().getContentType().toString()).contains("application/pdf");
    }

    @Test
    void downloadPdfReturnsPdfBytes() {
        byte[] pdfBytes = "PDF content".getBytes();

        when(pdfService.getPdfBytes(1L)).thenReturn(pdfBytes);

        ResponseEntity<byte[]> result = pdfController.downloadPdf(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(pdfBytes);
        assertThat(result.getHeaders().getContentType().toString()).contains("application/pdf");
    }
}


