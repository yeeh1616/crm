package com.example.crmbackend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.crmbackend.dto.ConversionRateDTO;
import com.example.crmbackend.service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AnalyticsControllerTest {

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private AnalyticsController analyticsController;

    @Test
    void getConversionRatesReturnsList() {
        ConversionRateDTO rate1 = ConversionRateDTO.builder()
                .source("Website")
                .totalLeads(100L)
                .convertedLeads(25L)
                .conversionRate(BigDecimal.valueOf(25.0))
                .build();

        ConversionRateDTO rate2 = ConversionRateDTO.builder()
                .source("Email")
                .totalLeads(50L)
                .convertedLeads(10L)
                .conversionRate(BigDecimal.valueOf(20.0))
                .build();

        List<ConversionRateDTO> rates = Arrays.asList(rate1, rate2);

        when(analyticsService.getConversionRatesBySource()).thenReturn(rates);

        ResponseEntity<List<ConversionRateDTO>> result = analyticsController.getConversionRates();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(rates);
        assertThat(result.getBody()).hasSize(2);
        assertThat(result.getBody().get(0).getSource()).isEqualTo("Website");
    }

    @Test
    void getSourceAnalysisReturnsList() {
        ConversionRateDTO analysis = ConversionRateDTO.builder()
                .source("Website")
                .totalLeads(100L)
                .convertedLeads(25L)
                .conversionRate(BigDecimal.valueOf(25.0))
                .build();

        List<ConversionRateDTO> analysisList = Arrays.asList(analysis);

        when(analyticsService.getSourceAnalysis()).thenReturn(analysisList);

        ResponseEntity<List<ConversionRateDTO>> result = analyticsController.getSourceAnalysis();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(analysisList);
        assertThat(result.getBody()).hasSize(1);
    }
}
