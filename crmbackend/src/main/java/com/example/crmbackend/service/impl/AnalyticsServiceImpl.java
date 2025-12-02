package com.example.crmbackend.service.impl;

import com.example.crmbackend.dto.ConversionRateDTO;
import com.example.crmbackend.repository.LeadRepository;
import com.example.crmbackend.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for analytics and reporting
 */
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final LeadRepository leadRepository;

    @Cacheable(value = "reportConversion")
    @Override
    public List<ConversionRateDTO> getConversionRatesBySource() {
        // Get all unique sources
        List<String> sources = leadRepository.findAll().stream()
                .map(lead -> lead.getSource() != null ? lead.getSource() : "Unknown")
                .distinct()
                .collect(Collectors.toList());

        return sources.stream().map(source -> {
            long totalLeads = leadRepository.countBySource(source);
            long convertedLeads = leadRepository.countConvertedBySource(source);
            
            BigDecimal conversionRate = totalLeads > 0
                    ? BigDecimal.valueOf(convertedLeads)
                            .divide(BigDecimal.valueOf(totalLeads), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;

            return ConversionRateDTO.builder()
                    .source(source)
                    .totalLeads(totalLeads)
                    .convertedLeads(convertedLeads)
                    .conversionRate(conversionRate)
                    .averageValue(BigDecimal.ZERO) // Can be enhanced with actual value calculation
                    .build();
        }).collect(Collectors.toList());
    }

    @Cacheable(value = "reportSource")
    @Override
    public List<ConversionRateDTO> getSourceAnalysis() {
        return getConversionRatesBySource();
    }
}

