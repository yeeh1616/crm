package com.example.crmbackend.service;

import com.example.crmbackend.dto.LoginRequest;
import com.example.crmbackend.dto.LoginResponse;
import com.example.crmbackend.entity.User;
import com.example.crmbackend.repository.UserRepository;
import com.example.crmbackend.security.JwtService;
import com.example.crmbackend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AuthService}.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_WhenUserExists_ReturnsLoginResponseWithToken() {
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("password");

        User user = User.builder()
                .id(1L)
                .username("john")
                .passwordHash("hashed")
                .role(User.Role.USER)
                .build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(org.springframework.security.core.userdetails.User.class)))
                .thenReturn("jwt-token");

        LoginResponse response = authService.login(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(any(org.springframework.security.core.userdetails.User.class));

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("john", response.getUsername());
        assertEquals("USER", response.getRole());
        assertEquals(1L, response.getUserId());
    }

    @Test
    void login_WhenUserDoesNotExist_ThrowsUsernameNotFoundException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("missing");
        request.setPassword("pwd");

        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authService.login(request));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(any());
    }
}


