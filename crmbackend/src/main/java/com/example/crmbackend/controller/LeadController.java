package com.example.crmbackend.controller;

import com.example.crmbackend.dto.LeadDTO;
import com.example.crmbackend.dto.PageResponse;
import com.example.crmbackend.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Lead CRUD operations
 */
@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
@Tag(name = "Leads", description = "Lead management API endpoints")
@SecurityRequirement(name = "bearerAuth")
public class LeadController {

    private final LeadService leadService;

    @GetMapping
    @Operation(summary = "Get all leads", description = "Retrieve paginated list of leads")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved leads")
    public ResponseEntity<PageResponse<LeadDTO>> getAllLeads(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Long customerId) {
        PageResponse<LeadDTO> response = leadService.getAllLeads(page, size, sortBy, sortDir, customerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lead by ID", description = "Retrieve a specific lead by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lead found"),
            @ApiResponse(responseCode = "404", description = "Lead not found")
    })
    public ResponseEntity<LeadDTO> getLeadById(
            @Parameter(description = "Lead ID", required = true) @PathVariable Long id) {
        LeadDTO lead = leadService.getLeadById(id);
        return ResponseEntity.ok(lead);
    }

    @PostMapping
    @Operation(summary = "Create lead", description = "Create a new sales lead")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lead created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<LeadDTO> createLead(@Valid @RequestBody LeadDTO dto) {
        LeadDTO created = leadService.createLead(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update lead", description = "Update an existing lead")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lead updated successfully"),
            @ApiResponse(responseCode = "404", description = "Lead not found")
    })
    public ResponseEntity<LeadDTO> updateLead(
            @Parameter(description = "Lead ID", required = true) @PathVariable Long id,
            @Valid @RequestBody LeadDTO dto) {
        LeadDTO updated = leadService.updateLead(id, dto);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/convert")
    @Operation(summary = "Convert lead to customer", description = "Convert a lead to a customer record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lead converted successfully"),
            @ApiResponse(responseCode = "400", description = "Lead already converted"),
            @ApiResponse(responseCode = "404", description = "Lead not found")
    })
    public ResponseEntity<LeadDTO> convertLeadToCustomer(
            @Parameter(description = "Lead ID", required = true) @PathVariable Long id) {
        LeadDTO converted = leadService.convertLeadToCustomer(id);
        return ResponseEntity.ok(converted);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete lead", description = "Delete a lead permanently")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Lead deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Lead not found")
    })
    public ResponseEntity<Void> deleteLead(
            @Parameter(description = "Lead ID", required = true) @PathVariable Long id) {
        leadService.deleteLead(id);
        return ResponseEntity.noContent().build();
    }
}

