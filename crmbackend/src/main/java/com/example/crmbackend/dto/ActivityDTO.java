package com.example.crmbackend.dto;

import com.example.crmbackend.entity.Activity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Activity operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDTO {
    private Long id;
    private Long leadId;
    
    @NotNull
    private Activity.Type type;
    
    private String content;
    private String outcome;
    private LocalDateTime nextFollowUpAt;
    private LocalDateTime createdAt;
    private boolean sentReminder;
    private boolean subscribedReminder;
}

