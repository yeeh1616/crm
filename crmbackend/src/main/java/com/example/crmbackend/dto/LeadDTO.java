package com.example.crmbackend.dto;

import com.example.crmbackend.entity.Lead;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for Lead operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadDTO {
    private Long id;
    private Long customerId;
    private String customerName;
    
    @NotBlank
    private String contactName;
    
    @NotBlank
    @Email
    private String contactEmail;
    
    private String contactPhone;
    private Lead.Stage stage;
    private Lead.Status status;
    private String source;
    private Long ownerId;
    private String ownerName;
    private BigDecimal value;
    private LocalDate expectedCloseDate;
    private LocalDateTime convertedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

