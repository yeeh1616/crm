package com.example.crmbackend.service.impl;

import com.example.crmbackend.repository.IdempotencyKeyRepository;
import com.example.crmbackend.repository.UserRepository;
import com.example.crmbackend.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service implementation for handling idempotency keys
 */
@Service
@RequiredArgsConstructor
public class IdempotencyServiceImpl implements IdempotencyService {

    private final UserRepository userRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String CUSTOMER_CREATE_KEY_PREFIX = "customer:create:";
    private static final long KEY_EXPIRATION_SECONDS = 10;

    /**
     * Check if user has an active create customer request in Redis
     * @param userId User ID
     * @param username Username (fallback if userId is null)
     * @return true if key exists (user should be blocked), false otherwise
     */
    @Override
    public boolean hasActiveCreateRequest(Long userId, String username) {
        String key = generateCustomerCreateKey(userId, username);
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * Set a create customer request key in Redis with expiration
     * @param userId User ID
     * @param username Username (fallback if userId is null)
     */
    @Override
    public void setActiveCreateRequest(Long userId, String username) {
        String key = generateCustomerCreateKey(userId, username);
        redisTemplate.opsForValue().set(key, "1", KEY_EXPIRATION_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Generate Redis key for customer create request
     * @param userId User ID
     * @param username Username (fallback if userId is null)
     * @return Redis key
     */
    private String generateCustomerCreateKey(Long userId, String username) {
        String identifier = userId != null ? String.valueOf(userId) : username;
        return CUSTOMER_CREATE_KEY_PREFIX + identifier;
    }
}

