package com.example.crmbackend.controller;

import com.example.crmbackend.dto.ActivityDTO;
import com.example.crmbackend.dto.PageResponse;
import com.example.crmbackend.service.ActivityService;
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
 * Controller for Activity CRUD operations
 */
@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
@Tag(name = "Activities", description = "Activity management API endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    @Operation(summary = "Get all activities", description = "Retrieve paginated list of activities")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved activities")
    public ResponseEntity<PageResponse<ActivityDTO>> getAllActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
//            @RequestParam(required = false) Long ownerId,
//            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long leadId) {
        PageResponse<ActivityDTO> response = activityService.getAllActivities(page, size, sortBy, sortDir, leadId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get activity by ID", description = "Retrieve a specific activity by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity found"),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    public ResponseEntity<ActivityDTO> getActivityById(
            @Parameter(description = "Activity ID", required = true) @PathVariable Long id) {
        ActivityDTO activity = activityService.getActivityById(id);
        return ResponseEntity.ok(activity);
    }

    @PostMapping
    @Operation(summary = "Create activity", description = "Create a new activity (call, email, meeting, etc.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<ActivityDTO> createActivity(@Valid @RequestBody ActivityDTO dto) {
        ActivityDTO created = activityService.createActivity(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update activity", description = "Update an existing activity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity updated successfully"),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    public ResponseEntity<ActivityDTO> updateActivity(
            @Parameter(description = "Activity ID", required = true) @PathVariable Long id,
            @Valid @RequestBody ActivityDTO dto) {
        ActivityDTO updated = activityService.updateActivity(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete activity", description = "Delete an activity permanently")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Activity deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Activity not found")
    })
    public ResponseEntity<Void> deleteActivity(
            @Parameter(description = "Activity ID", required = true) @PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }
}

