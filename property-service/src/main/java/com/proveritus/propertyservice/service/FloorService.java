package com.proveritus.propertyservice.service;

import com.proveritus.propertyservice.dto.FloorDTO;
import com.proveritus.propertyservice.dto.FloorOccupancyStats;
import com.proveritus.cloudutility.exception.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FloorService {
    FloorDTO createFloor(FloorDTO floorDTO);

    FloorDTO getFloorById(Long id) throws EntityNotFoundException;

    List<FloorDTO> getFloorsByPropertyId(Long propertyId);

    FloorDTO updateFloor(Long id, FloorDTO floorDTO) throws EntityNotFoundException;

    Page<FloorDTO> getFloorsByPropertyId(Long propertyId, Pageable pageable);

    void deleteFloor(Long id) throws EntityNotFoundException;

    FloorOccupancyStats getFloorOccupancyStats(Long id) throws EntityNotFoundException;

    void updateFloorOccupancyStats(Long floorId);
}

