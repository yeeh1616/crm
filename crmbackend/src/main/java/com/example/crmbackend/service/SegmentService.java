package com.example.crmbackend.service;

import com.example.crmbackend.dto.SegmentDTO;

import java.util.List;

/**
 * Service interface for segment operations
 */
public interface SegmentService {
    List<SegmentDTO> getSegments();
}
