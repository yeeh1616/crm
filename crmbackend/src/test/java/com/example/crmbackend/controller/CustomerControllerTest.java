package com.example.crmbackend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.example.crmbackend.dto.CustomerDTO;
import com.example.crmbackend.dto.PageResponse;
import com.example.crmbackend.entity.User;
import com.example.crmbackend.exception.TooManyRequestsException;
import com.example.crmbackend.repository.UserRepository;
import com.example.crmbackend.service.CustomerService;
import com.example.crmbackend.service.IdempotencyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private IdempotencyService idempotencyService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomerController customerController;

    @Test
    void getAllCustomersReturnsPageResponse() {
        PageResponse<CustomerDTO> response = PageResponse.<CustomerDTO>builder()
                .content(Collections.emptyList())
                .page(0)
                .size(10)
                .totalElements(0L)
                .totalPages(0)
                .first(true)
                .last(true)
                .build();

        when(customerService.getAllCustomers(anyInt(), anyInt(), anyString(), anyString(), any(), any(), any()))
                .thenReturn(response);

        ResponseEntity<PageResponse<CustomerDTO>> result = customerController.getAllCustomers(
                0, 10, "id", "asc", null, null, null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
        assertThat(result.getBody().getPage()).isEqualTo(0);
        assertThat(result.getBody().getSize()).isEqualTo(10);
    }

    @Test
    void getTotalReturnsLong() {
        when(customerService.getTotal(any(), any(), any())).thenReturn(100L);

        ResponseEntity<Long> result = customerController.getTotal(null, null, null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(100L);
    }

    @Test
    void getCustomerByIdReturnsCustomerDTO() {
        CustomerDTO customer = CustomerDTO.builder()
                .id(1L)
                .name("Test Customer")
                .email("test@example.com")
                .build();

        when(customerService.getCustomerById(1L)).thenReturn(customer);

        ResponseEntity<CustomerDTO> result = customerController.getCustomerById(1L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(customer);
        assertThat(result.getBody().getId()).isEqualTo(1L);
        assertThat(result.getBody().getName()).isEqualTo("Test Customer");
    }

    @Test
    void createCustomerReturnsCustomerDTO() {
        CustomerDTO input = CustomerDTO.builder()
                .name("New Customer")
                .email("test@example.com")
                .ownerId(1L)
                .build();

        CustomerDTO created = CustomerDTO.builder()
                .id(1L)
                .name("New Customer")
                .email("test@example.com")
                .ownerId(1L)
                .build();

        User user = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(idempotencyService.hasActiveCreateRequest(1L, "testuser")).thenReturn(false);
        when(customerService.createCustomer(input)).thenReturn(created);

        ResponseEntity<CustomerDTO> result = customerController.createCustomer(input, null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(created);
        verify(idempotencyService).setActiveCreateRequest(1L, "testuser");
    }

    @Test
    void createCustomerWithActiveRequestThrowsTooManyRequestsException() {
        CustomerDTO input = CustomerDTO.builder()
                .name("New Customer")
                .email("test@example.com")
                .ownerId(1L)
                .build();

        User user = User.builder()
                .id(1L)
                .username("testuser")
                .build();

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(idempotencyService.hasActiveCreateRequest(1L, "testuser")).thenReturn(true);

        assertThatThrownBy(() -> customerController.createCustomer(input, null))
                .isInstanceOf(TooManyRequestsException.class)
                .hasMessageContaining("Please wait before creating another customer");

        verify(customerService, never()).createCustomer(any());
    }

    @Test
    void updateCustomerReturnsUpdatedDTO() {
        CustomerDTO input = CustomerDTO.builder()
                .id(1L)
                .name("Updated Customer")
                .email("updated@example.com")
                .build();

        when(customerService.updateCustomer(input)).thenReturn(input);

        ResponseEntity<CustomerDTO> result = customerController.updateCustomer(input, null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(input);
        assertThat(result.getBody().getName()).isEqualTo("Updated Customer");
    }

    @Test
    void deleteCustomerReturnsNoContent() {
        doNothing().when(customerService).deleteCustomer(1L, false);

        ResponseEntity<Void> result = customerController.deleteCustomer(1L, false);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(customerService).deleteCustomer(1L, false);
    }
}
