package com.example.crmbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for conversion rate analytics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversionRateDTO {
    private String source;
    private Long totalLeads;
    private Long convertedLeads;
    private BigDecimal conversionRate;
    private BigDecimal averageValue;
}

