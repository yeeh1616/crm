package com.example.crmbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for export task status
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportStatusDTO {
    private String taskId;
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED
    private String downloadUrl;
    private String errorMessage;
}

