package com.example.crmbackend.dto;

import com.example.crmbackend.entity.Segment;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Segment operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SegmentDTO {
    @NotNull
    private Long id;

    @NotNull
    private String name;
}

