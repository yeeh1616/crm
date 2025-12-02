package com.example.crmbackend.controller;

import com.example.crmbackend.dto.ConversionRateDTO;
import com.example.crmbackend.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for analytics and reporting endpoints
 */
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Analytics and reporting API endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/conversion-rates")
    @Operation(summary = "Get conversion rates", description = "Get lead conversion rates grouped by source")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved conversion rates",
            content = @Content(schema = @Schema(implementation = ConversionRateDTO.class)))
    public ResponseEntity<List<ConversionRateDTO>> getConversionRates() {
        List<ConversionRateDTO> rates = analyticsService.getConversionRatesBySource();
        return ResponseEntity.ok(rates);
    }

    @GetMapping("/source-analysis")
    @Operation(summary = "Get source analysis", description = "Get detailed analysis of lead sources and conversion metrics")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved source analysis",
            content = @Content(schema = @Schema(implementation = ConversionRateDTO.class)))
    public ResponseEntity<List<ConversionRateDTO>> getSourceAnalysis() {
        List<ConversionRateDTO> analysis = analyticsService.getSourceAnalysis();
        return ResponseEntity.ok(analysis);
    }
}

