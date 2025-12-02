package com.example.crmbackend.service.impl;

import com.example.crmbackend.dto.ExportRequestDTO;
import com.example.crmbackend.dto.ExportStatusDTO;
import com.example.crmbackend.service.ExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service implementation for handling export requests (PDF/CSV/XLSX)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExportServiceImpl implements ExportService {

    private final RabbitTemplate rabbitTemplate;
    private final Map<String, ExportStatusDTO> exportStatusCache = new HashMap<>();

    /**
     * Initiate export task and send to message queue
     */
    @Override
    public ExportStatusDTO initiateExport(ExportRequestDTO request, Long userId) {
        String taskId = UUID.randomUUID().toString();
        
        ExportStatusDTO status = ExportStatusDTO.builder()
                .taskId(taskId)
                .status("PENDING")
                .build();
        
        exportStatusCache.put(taskId, status);

        // Send to queue
        Map<String, Object> message = new HashMap<>();
        message.put("taskId", taskId);
        message.put("format", request.getFormat());
        message.put("type", request.getType());
        message.put("userId", userId);
        
        rabbitTemplate.convertAndSend(EXPORT_QUEUE, message);
        log.info("Export task {} queued for processing", taskId);

        return status;
    }

    /**
     * Get export status
     */
    @Cacheable(value = "exportTasks", key = "#taskId")
    @Override
    public ExportStatusDTO getExportStatus(String taskId) {
        return exportStatusCache.getOrDefault(taskId, 
                ExportStatusDTO.builder()
                        .taskId(taskId)
                        .status("NOT_FOUND")
                        .build());
    }

    /**
     * Update export status (called by message consumer)
     */
    @Override
    public void updateExportStatus(String taskId, String status, String downloadUrl, String errorMessage) {
        ExportStatusDTO exportStatus = exportStatusCache.get(taskId);
        if (exportStatus != null) {
            exportStatus.setStatus(status);
            exportStatus.setDownloadUrl(downloadUrl);
            exportStatus.setErrorMessage(errorMessage);
            exportStatusCache.put(taskId, exportStatus);
        }
    }
}

