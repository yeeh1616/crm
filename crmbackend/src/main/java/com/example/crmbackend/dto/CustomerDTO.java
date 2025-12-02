package com.example.crmbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Customer operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    
    @NotBlank
    private String name;
    
    @NotBlank
    @Email
    private String email;
    
    private String phone;
    private String company;
    private String title;
    private String source;
    private String country;
    private String city;
    private List<TagDTO> tags;
    private Long segmentId;
    private String segmentName;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer activityScore;
}

