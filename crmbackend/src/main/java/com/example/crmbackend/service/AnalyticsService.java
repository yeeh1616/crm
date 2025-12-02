package com.example.crmbackend.service;

import com.example.crmbackend.dto.ConversionRateDTO;

import java.util.List;

/**
 * Service interface for analytics and reporting
 */
public interface AnalyticsService {
    List<ConversionRateDTO> getConversionRatesBySource();
    List<ConversionRateDTO> getSourceAnalysis();
}
