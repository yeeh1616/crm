package com.example.crmbackend.service.impl;

import com.example.crmbackend.dto.SegmentDTO;
import com.example.crmbackend.entity.Segment;
import com.example.crmbackend.mapper.SegmentMapper;
import com.example.crmbackend.repository.SegmentRepository;
import com.example.crmbackend.service.SegmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for segment operations
 */
@Service
@RequiredArgsConstructor
public class SegmentServiceImpl implements SegmentService {

    private final SegmentRepository segmentRepository;
    private final SegmentMapper segmentMapper;

    @Override
    public List<SegmentDTO> getSegments() {
        List<Segment> segments = segmentRepository.findAll();

        return segments.stream().map(segmentMapper::toDTO).collect(Collectors.toList());
    }
}

