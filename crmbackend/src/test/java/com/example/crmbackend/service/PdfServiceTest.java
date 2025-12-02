package com.example.crmbackend.service;

import com.example.crmbackend.dto.ConversionRateDTO;
import com.example.crmbackend.entity.Customer;
import com.example.crmbackend.repository.CustomerRepository;
import com.example.crmbackend.service.impl.AnalyticsServiceImpl;
import com.example.crmbackend.service.impl.PdfServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PdfServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AnalyticsServiceImpl analyticsService;

    @InjectMocks
    private PdfServiceImpl pdfService;

    private Customer testCustomer;
    private ConversionRateDTO conversionRateDTO;

    @BeforeEach
    void setUp() {
        testCustomer = Customer.builder()
                .id(1L)
                .name("Test Customer")
                .email("test@example.com")
                .phone("1234567890")
                .title("Manager")
                .source("Website")
                .createdAt(LocalDateTime.now())
                .build();

        conversionRateDTO = ConversionRateDTO.builder()
                .source("Website")
                .totalLeads(100L)
                .convertedLeads(20L)
                .conversionRate(BigDecimal.valueOf(20.0))
                .averageValue(BigDecimal.valueOf(1000.0))
                .build();
    }

    @Test
    void getPdfBytes_GeneratesPdfSuccessfully() {
        when(customerRepository.findAll()).thenReturn(List.of(testCustomer));
        when(analyticsService.getConversionRatesBySource()).thenReturn(List.of(conversionRateDTO));

        byte[] pdfBytes = pdfService.getPdfBytes(1L);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        verify(customerRepository).findAll();
        verify(analyticsService).getConversionRatesBySource();
    }

    @Test
    void getPdfBytes_HandlesEmptyData() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());
        when(analyticsService.getConversionRatesBySource()).thenReturn(Collections.emptyList());

        byte[] pdfBytes = pdfService.getPdfBytes(1L);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    void getPdfBytes_HandlesMultipleCustomers() {
        Customer customer2 = Customer.builder()
                .id(2L)
                .name("Customer 2")
                .email("customer2@example.com")
                .phone("0987654321")
                .title("Director")
                .source("Email")
                .createdAt(LocalDateTime.now())
                .build();

        when(customerRepository.findAll()).thenReturn(List.of(testCustomer, customer2));
        when(analyticsService.getConversionRatesBySource()).thenReturn(List.of(conversionRateDTO));

        byte[] pdfBytes = pdfService.getPdfBytes(1L);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }
}


