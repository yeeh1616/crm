package com.example.crmbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for export request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportRequestDTO {
    private String format; // PDF, CSV, XLSX
    private String type; // customers, leads, activities, reports
    private Long userId; // Optional filter
}

