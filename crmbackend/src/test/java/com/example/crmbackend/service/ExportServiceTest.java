package com.example.crmbackend.service;

import com.example.crmbackend.dto.ExportRequestDTO;
import com.example.crmbackend.dto.ExportStatusDTO;
import com.example.crmbackend.service.impl.ExportServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link ExportService}.
 */
@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ExportServiceImpl exportService;

    @Test
    void initiateExport_SendsMessageAndStoresStatus() {
        ExportRequestDTO request = new ExportRequestDTO();
        request.setFormat("CSV");
        request.setType("CUSTOMER");

        ExportStatusDTO status = exportService.initiateExport(request, 1L);

        assertNotNull(status.getTaskId());
        assertEquals("PENDING", status.getStatus());

        verify(rabbitTemplate).convertAndSend(eq(ExportService.EXPORT_QUEUE), any(Object.class));
    }

    @Test
    void getExportStatus_NotFound_ReturnsNotFoundStatus() {
        ExportStatusDTO status = exportService.getExportStatus("missing");

        assertEquals("NOT_FOUND", status.getStatus());
    }

    @Test
    void updateExportStatus_UpdatesExistingStatus() {
        ExportRequestDTO request = new ExportRequestDTO();
        request.setFormat("CSV");
        request.setType("CUSTOMER");

        ExportStatusDTO initial = exportService.initiateExport(request, 1L);

        exportService.updateExportStatus(initial.getTaskId(), "COMPLETED", "/download/url", null);

        ExportStatusDTO updated = exportService.getExportStatus(initial.getTaskId());
        assertEquals("COMPLETED", updated.getStatus());
        assertEquals("/download/url", updated.getDownloadUrl());
        assertNull(updated.getErrorMessage());
    }
}


