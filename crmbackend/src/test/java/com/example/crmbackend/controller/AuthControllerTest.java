package com.example.crmbackend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.crmbackend.dto.LoginRequest;
import com.example.crmbackend.dto.LoginResponse;
import com.example.crmbackend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void loginReturnsTokenAndUser() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("password123");

        LoginResponse response = LoginResponse.builder()
                .token("token-123")
                .username("user")
                .role("USER")
                .userId(1L)
                .build();

        when(authService.login(request)).thenReturn(response);

        ResponseEntity<LoginResponse> result = authController.login(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
        assertThat(result.getBody().getToken()).isEqualTo("token-123");
        assertThat(result.getBody().getUsername()).isEqualTo("user");
        assertThat(result.getBody().getRole()).isEqualTo("USER");
        assertThat(result.getBody().getUserId()).isEqualTo(1L);
    }
}
