package com.example.crmbackend.controller;

import com.example.crmbackend.dto.SegmentDTO;
import com.example.crmbackend.service.SegmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for segments CRUD operations
 */
@RestController
@RequestMapping("/api/segments")
@RequiredArgsConstructor
@Tag(name = "Segments", description = "Customer segment management API endpoints")
@SecurityRequirement(name = "bearerAuth")
public class SegmentController {

    private final SegmentService segmentService;

    @GetMapping
    @Operation(summary = "Get all segments", description = "Retrieve all available customer segments")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved segments",
            content = @Content(schema = @Schema(implementation = SegmentDTO.class)))
    public ResponseEntity<List<SegmentDTO>> getAllActivities() {
        List<SegmentDTO> response = segmentService.getSegments();
        return ResponseEntity.ok(response);
    }
}

