package com.proveritus.propertyservice.controller;

import com.proveritus.propertyservice.dto.FloorDTO;
import com.proveritus.propertyservice.dto.FloorOccupancyStats;
import com.proveritus.propertyservice.service.FloorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/floors")
@Tag(name = "Floors", description = "APIs for managing building floors")
public class FloorController {
    private final FloorService floorService;

    @PostMapping
    @Operation(summary = "Create a new floor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Floor created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Floor already exists")
    })
    public ResponseEntity<FloorDTO> createFloor(@Valid @RequestBody FloorDTO floorDTO) {
        log.info("Creating floor for property ID: {}", floorDTO.getPropertyId());
        FloorDTO createdFloor = floorService.createFloor(floorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFloor);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a floor by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Floor found"),
            @ApiResponse(responseCode = "404", description = "Floor not found")
    })
    public ResponseEntity<FloorDTO> getFloorById(
            @Parameter(description = "ID of the floor to retrieve") @PathVariable Long id) {
        log.debug("Fetching floor with ID: {}", id);
        return ResponseEntity.ok(floorService.getFloorById(id));
    }

    @GetMapping
    @Operation(summary = "Get floors by property ID with optional pagination")
    public ResponseEntity<?> getFloorsByPropertyId(
            @RequestParam Long propertyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        log.debug("Fetching floors for property ID: {}, page: {}, size: {}", propertyId, page, size);

        if (size <= 0) {
            // Return all floors without pagination
            return ResponseEntity.ok(floorService.getFloorsByPropertyId(propertyId));
        } else {
            // Return paginated results
            Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            Page<FloorDTO> floors = floorService.getFloorsByPropertyId(propertyId, pageable);
            return ResponseEntity.ok(floors);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing floor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Floor updated successfully"),
            @ApiResponse(responseCode = "404", description = "Floor not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<FloorDTO> updateFloor(
            @Parameter(description = "ID of the floor to update") @PathVariable Long id,
            @Valid @RequestBody FloorDTO floorDTO) {
        log.info("Updating floor with ID: {}", id);
        return ResponseEntity.ok(floorService.updateFloor(id, floorDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a floor by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Floor deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Floor not found"),
            @ApiResponse(responseCode = "409", description = "Floor has units and cannot be deleted")
    })
    public ResponseEntity<Void> deleteFloor(
            @Parameter(description = "ID of the floor to delete") @PathVariable Long id) {
        log.info("Deleting floor with ID: {}", id);
        floorService.deleteFloor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/occupancy-stats")
    @Operation(summary = "Get occupancy statistics for a floor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Floor not found")
    })
    public ResponseEntity<FloorOccupancyStats> getFloorOccupancyStats(
            @Parameter(description = "ID of the floor") @PathVariable Long id) {
        log.debug("Fetching occupancy stats for floor ID: {}", id);
        return ResponseEntity.ok(floorService.getFloorOccupancyStats(id));
    }

    @PostMapping("/{id}/refresh-occupancy")
    @Operation(summary = "Refresh occupancy statistics for a floor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Occupancy stats refreshed successfully"),
            @ApiResponse(responseCode = "404", description = "Floor not found")
    })
    public ResponseEntity<Void> refreshFloorOccupancy(
            @Parameter(description = "ID of the floor") @PathVariable Long id) {
        log.info("Refreshing occupancy stats for floor ID: {}", id);
        floorService.updateFloorOccupancyStats(id);
        return ResponseEntity.ok().build();
    }
}
