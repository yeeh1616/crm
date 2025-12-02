package com.example.crmbackend.dto;

import com.example.crmbackend.entity.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Segment operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagDTO {
    @NotNull
    private Long id;

    @NotNull
    private String name;
}

