package com.example.crmbackend.service;

import java.util.Map;

/**
 * Service interface for processing export tasks from message queue
 */
public interface ExportProcessorService {
    void processExport(Map<String, Object> message);
}
