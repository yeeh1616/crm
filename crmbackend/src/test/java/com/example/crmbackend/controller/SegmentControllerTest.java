package com.example.crmbackend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.crmbackend.dto.SegmentDTO;
import com.example.crmbackend.service.SegmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class SegmentControllerTest {

    @Mock
    private SegmentService segmentService;

    @InjectMocks
    private SegmentController segmentController;

    @Test
    void getSegmentsReturnsList() {
        SegmentDTO segment1 = SegmentDTO.builder()
                .id(1L)
                .name("VIP")
                .build();

        SegmentDTO segment2 = SegmentDTO.builder()
                .id(2L)
                .name("Standard")
                .build();

        List<SegmentDTO> segments = Arrays.asList(segment1, segment2);

        when(segmentService.getSegments()).thenReturn(segments);

        ResponseEntity<List<SegmentDTO>> result = segmentController.getAllActivities();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(segments);
        assertThat(result.getBody()).hasSize(2);
        assertThat(result.getBody().get(0).getName()).isEqualTo("VIP");
    }
}


