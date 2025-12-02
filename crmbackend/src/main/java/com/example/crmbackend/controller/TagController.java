package com.example.crmbackend.controller;

import com.example.crmbackend.dto.TagDTO;
import com.example.crmbackend.service.TagService;
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
 * Controller for tags CRUD operations
 */
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Tag(name = "Tags", description = "Tag management API endpoints")
@SecurityRequirement(name = "bearerAuth")
public class TagController {

    private final TagService tagService;

    @GetMapping
    @Operation(summary = "Get all tags", description = "Retrieve all available tags")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved tags",
            content = @Content(schema = @Schema(implementation = TagDTO.class)))
    public ResponseEntity<List<TagDTO>> getAllActivities() {
        List<TagDTO> response = tagService.getTags();
        return ResponseEntity.ok(response);
    }
}

