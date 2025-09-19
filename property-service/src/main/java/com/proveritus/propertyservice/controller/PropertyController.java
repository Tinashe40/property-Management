package com.proveritus.propertyservice.controller;

import com.proveritus.propertyservice.audit.annotation.Auditable;
import com.proveritus.propertyservice.dto.PropertyDTO;
import com.proveritus.propertyservice.dto.PropertyStatsDTO;
import com.proveritus.propertyservice.enums.PropertyType;
import com.proveritus.propertyservice.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
@Tag(name = "Properties", description = "APIs for managing properties")
@SecurityRequirement(name = "bearAuth")
public class PropertyController {
    private final PropertyService propertyService;

    @Auditable
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROPERTY_MANAGER')")
    @Operation(summary = "Create a new property")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Property created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Property already exists")
    })
    public ResponseEntity<PropertyDTO> createProperty(@Valid @RequestBody PropertyDTO propertyDTO) {
        log.info("Creating property: {}", propertyDTO.getName());
        PropertyDTO createdProperty = propertyService.createProperty(propertyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProperty);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a property by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Property found"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
    public ResponseEntity<PropertyDTO> getPropertyById(
            @Parameter(description = "ID of the property to retrieve") @PathVariable Long id) {
        log.debug("Fetching property with ID: {}", id);
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }


    @GetMapping
    @Operation(summary = "Get all properties with optional filtering and pagination")
    public ResponseEntity<Page<PropertyDTO>> getProperties(
            @RequestParam(required = false) PropertyType propertyType,
            Pageable pageable) {

        log.debug("Fetching properties with type: {}, pageable: {}", propertyType, pageable);

        if (propertyType != null) {
            return ResponseEntity.ok(propertyService.getAllPropertiesByType(propertyType, pageable));
        } else {
            return ResponseEntity.ok(propertyService.getAllProperties(pageable));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing property")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Property updated successfully"),
            @ApiResponse(responseCode = "404", description = "Property not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Property name already exists")
    })
    public ResponseEntity<PropertyDTO> updateProperty(
            @Parameter(description = "ID of the property to update") @PathVariable Long id,
            @Valid @RequestBody PropertyDTO propertyDTO) {
        log.info("Updating property with ID: {}", id);
        return ResponseEntity.ok(propertyService.updateProperty(id, propertyDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a property by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Property deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Property not found"),
            @ApiResponse(responseCode = "409", description = "Property has floors/units and cannot be deleted")
    })
    public ResponseEntity<Void> deleteProperty(
            @Parameter(description = "ID of the property to delete") @PathVariable Long id) {
        log.info("Deleting property with ID: {}", id);
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search properties by name or address")
    public ResponseEntity<Page<PropertyDTO>> searchProperties(
            @RequestParam String query,
            Pageable pageable) {

        log.debug("Searching properties with query: {}", query);
        return ResponseEntity.ok(propertyService.searchProperties(query, pageable));
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "Get statistics for a property")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Property not found")
    })
    public ResponseEntity<PropertyStatsDTO> getPropertyStats(
            @Parameter(description = "ID of the property") @PathVariable Long id) {
        log.debug("Fetching stats for property ID: {}", id);
        return ResponseEntity.ok(propertyService.getPropertyStats(id));
    }

    @GetMapping("/count")
    @Operation(summary = "Get total number of properties")
    public ResponseEntity<Long> getPropertiesCount() {
        log.debug("Fetching properties count");
        return ResponseEntity.ok(propertyService.getTotalPropertiesCount());
    }
}
