package com.example.crmbackend.service;

/**
 * Service interface for handling idempotency keys
 */
public interface IdempotencyService {
    boolean hasActiveCreateRequest(Long userId, String username);
    void setActiveCreateRequest(Long userId, String username);
}
