package com.example.crmbackend.service;

import com.example.crmbackend.dto.LoginRequest;
import com.example.crmbackend.dto.LoginResponse;

/**
 * Service interface for authentication operations
 */
public interface AuthService {
    LoginResponse login(LoginRequest request);
}
