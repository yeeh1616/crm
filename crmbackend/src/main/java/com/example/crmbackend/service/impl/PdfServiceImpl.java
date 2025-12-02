package com.example.crmbackend.service.impl;

import com.example.crmbackend.dto.ConversionRateDTO;
import com.example.crmbackend.entity.Customer;
import com.example.crmbackend.repository.CustomerRepository;
import com.example.crmbackend.service.AnalyticsService;
import com.example.crmbackend.service.PdfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import java.util.List;

/**
 * Service implementation for PDF generation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PdfServiceImpl implements PdfService {
    private final CustomerRepository customerRepository;
    private final AnalyticsService analyticsService;

    @Override
    public byte[] getPdfBytes(Long id) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            List<Customer> customers = customerRepository.findAll();
            List<ConversionRateDTO> crDtos = analyticsService.getConversionRatesBySource();

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("CRM Customer Report")
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Owner ID: " + id));

            Table analyticsInfoTable = new Table(5);

            // --- Header row ---
            analyticsInfoTable.addHeaderCell("Source");
            analyticsInfoTable.addHeaderCell("Total Leads");
            analyticsInfoTable.addHeaderCell("Converted Leads");
            analyticsInfoTable.addHeaderCell("Conversion Rate");
            analyticsInfoTable.addHeaderCell("Average Value");

            // --- Data rows ---
            crDtos.forEach(crDto -> {
                analyticsInfoTable.addCell(String.valueOf(crDto.getSource() != null ? crDto.getSource() : "N/A"));
                analyticsInfoTable.addCell(String.valueOf(crDto.getTotalLeads() != null ? crDto.getTotalLeads() : "N/A"));
                analyticsInfoTable.addCell(String.valueOf(crDto.getConvertedLeads() != null ? crDto.getConvertedLeads() : "N/A" ));
                analyticsInfoTable.addCell(String.valueOf(crDto.getConversionRate() != null ? crDto.getConversionRate() : "N/A"));
                analyticsInfoTable.addCell(String.valueOf(crDto.getAverageValue() != null ? crDto.getAverageValue() : "N/A"));
            });

            document.add(analyticsInfoTable);

            document.add(new Paragraph("\n"));

            Table customerInfoTable = new Table(7);

            // --- Header row ---
            customerInfoTable.addHeaderCell("ID");
            customerInfoTable.addHeaderCell("Name");
            customerInfoTable.addHeaderCell("Email");
            customerInfoTable.addHeaderCell("Phone");
            customerInfoTable.addHeaderCell("Title");
            customerInfoTable.addHeaderCell("Source");
            customerInfoTable.addHeaderCell("Created At");

            // --- Data rows ---
            customers.forEach(customer -> {
                customerInfoTable.addCell(String.valueOf(customer.getId()));
                customerInfoTable.addCell(customer.getName() != null ? customer.getName() : "N/A");
                customerInfoTable.addCell(customer.getEmail() != null ? customer.getEmail() : "N/A");
                customerInfoTable.addCell(customer.getPhone() != null ? customer.getPhone() : "N/A");
                customerInfoTable.addCell(customer.getTitle() != null ? customer.getTitle() : "N/A");
                customerInfoTable.addCell(customer.getSource() != null ? customer.getSource() : "N/A");
                customerInfoTable.addCell(customer.getCreatedAt() != null ? customer.getCreatedAt().toString() : "N/A");
            });

            document.add(customerInfoTable);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}

