package com.example.crmbackend.service;

import com.example.crmbackend.dto.ExportRequestDTO;
import com.example.crmbackend.dto.ExportStatusDTO;

/**
 * Service interface for handling export requests (PDF/CSV/XLSX)
 */
public interface ExportService {
    String EXPORT_QUEUE = "export.queue";
    ExportStatusDTO initiateExport(ExportRequestDTO request, Long userId);
    ExportStatusDTO getExportStatus(String taskId);
    void updateExportStatus(String taskId, String status, String downloadUrl, String errorMessage);
}
