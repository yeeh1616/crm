package com.example.crmbackend.service;

import com.example.crmbackend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link JwtService}.
 */
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();

        // Inject test secret/expiration via reflection because fields are normally populated from properties.
        Field secretField = JwtService.class.getDeclaredField("secretKey");
        secretField.setAccessible(true);
        // base64 for "test-jwt-secret-test-jwt-secret-123456"
        secretField.set(jwtService, "dGVzdC1qd3Qtc2VjcmV0LXRlc3Qtand0LXNlY3JldC0xMjM0NTY=");

        Field expField = JwtService.class.getDeclaredField("expiration");
        expField.setAccessible(true);
        expField.set(jwtService, 3600000L);
    }

    @Test
    void generateAndValidateToken_Succeeds() {
        User userDetails = new User("alice", "password", Collections.emptyList());

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertEquals("alice", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_WithDifferentUser_Fails() {
        User userDetails = new User("bob", "password", Collections.emptyList());
        User otherUser = new User("alice", "password", Collections.emptyList());

        String token = jwtService.generateToken(otherUser);

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }
}


