package com.example.crmbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for login request
 */
@Data
public class LoginRequest {
    @NotBlank
    private String username;
    
    @NotBlank
    private String password;
}

