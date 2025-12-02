package com.example.crmbackend.service;

/**
 * Service interface for PDF generation
 */
public interface PdfService {
    byte[] getPdfBytes(Long id);
}
