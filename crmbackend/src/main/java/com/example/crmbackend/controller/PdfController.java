package com.example.crmbackend.controller;

import com.example.crmbackend.service.PdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for export operations
 */
@RestController
@RequestMapping("/api/exports")
@RequiredArgsConstructor
@Tag(name = "Exports", description = "Export and PDF generation API endpoints")
@SecurityRequirement(name = "bearerAuth")
public class PdfController {
    private final PdfService pdfService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get PDF report", description = "Generate and return PDF report for a user (inline view)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF generated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<byte[]> getPdf(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {
        byte[] pdfBytes = pdfService.getPdfBytes(userId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("crm_report.pdf").build());
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/{userId}/download")
    @Operation(summary = "Download PDF report", description = "Generate and download PDF report for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PDF downloaded successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<byte[]> downloadPdf(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {
        byte[] pdfBytes = pdfService.getPdfBytes(userId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("crm_report.pdf").build());
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}

