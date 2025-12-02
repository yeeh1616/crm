package com.example.crmbackend.service;

import com.example.crmbackend.dto.ConversionRateDTO;
import com.example.crmbackend.entity.Lead;
import com.example.crmbackend.repository.LeadRepository;
import com.example.crmbackend.service.impl.AnalyticsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AnalyticsService}.
 */
@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private LeadRepository leadRepository;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    @Test
    void getConversionRatesBySource_ComputesRates() {
        Lead lead1 = new Lead();
        lead1.setSource("WEB");
        Lead lead2 = new Lead();
        lead2.setSource("WEB");
        when(leadRepository.findAll()).thenReturn(List.of(lead1, lead2));
        when(leadRepository.countBySource("WEB")).thenReturn(2L);
        when(leadRepository.countConvertedBySource("WEB")).thenReturn(1L);

        List<ConversionRateDTO> result = analyticsService.getConversionRatesBySource();

        assertEquals(1, result.size());
        ConversionRateDTO dto = result.get(0);
        assertEquals("WEB", dto.getSource());
        assertEquals(2L, dto.getTotalLeads());
        assertEquals(1L, dto.getConvertedLeads());
        assertEquals(50, dto.getConversionRate().intValue());
    }
}


