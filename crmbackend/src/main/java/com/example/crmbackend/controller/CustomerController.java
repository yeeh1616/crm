package com.example.crmbackend.controller;

import com.example.crmbackend.dto.CustomerDTO;
import com.example.crmbackend.dto.PageResponse;
import com.example.crmbackend.exception.TooManyRequestsException;
import com.example.crmbackend.service.CustomerService;
import com.example.crmbackend.service.IdempotencyService;
import com.example.crmbackend.entity.User;
import com.example.crmbackend.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Customer CRUD operations
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer management API endpoints")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final CustomerService customerService;
    private final IdempotencyService idempotencyService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieve paginated list of customers with optional filters")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved customers")
    public ResponseEntity<PageResponse<CustomerDTO>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) Long segment,
            @RequestParam(required = false) List<Long> tags) {
        PageResponse<CustomerDTO> response = customerService.getAllCustomers(page, size, sortBy, sortDir, ownerId, segment, tags);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/total")
    @Operation(summary = "Get total customer count", description = "Get total count of customers matching filters")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved total count")
    public ResponseEntity<Long> getTotal(
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) Long segment,
            @RequestParam(required = false) List<Long> tags) {
        Long total = customerService.getTotal(ownerId, segment, tags);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieve a specific customer by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerDTO> getCustomerById(
            @Parameter(description = "Customer ID", required = true) @PathVariable Long id) {
        CustomerDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    @PostMapping
    @Operation(summary = "Create customer", description = "Create a new customer. Prevents duplicate creation within 10 seconds")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "429", description = "Too many requests - please wait before creating another customer")
    })
    public ResponseEntity<CustomerDTO> createCustomer(
            @Valid @RequestBody CustomerDTO dto,
            HttpServletRequest request) {
        // Get authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user has an active create request in Redis (10 seconds expiration)
        if (idempotencyService.hasActiveCreateRequest(user.getId(), user.getUsername())) {
            throw new TooManyRequestsException("Please wait before creating another customer. Duplicate requests are not allowed within 10 seconds.");
        }

        // Set the key in Redis to prevent duplicate requests
        idempotencyService.setActiveCreateRequest(user.getId(), user.getUsername());

        // Create the customer
        CustomerDTO created = customerService.createCustomer(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping
    @Operation(summary = "Update customer", description = "Update an existing customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<CustomerDTO> updateCustomer(
            @Valid @RequestBody CustomerDTO dto,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        
        CustomerDTO updated = customerService.updateCustomer(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer", description = "Delete a customer. Users can only soft delete, admins can permanently delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Void> deleteCustomer(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean permanent) {
        customerService.deleteCustomer(id, permanent);
        return ResponseEntity.noContent().build();
    }
}

