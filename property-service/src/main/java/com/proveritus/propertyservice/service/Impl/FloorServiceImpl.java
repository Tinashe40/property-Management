package com.proveritus.propertyservice.service.Impl;

import com.proveritus.propertyservice.dto.FloorDTO;
import com.proveritus.propertyservice.dto.FloorOccupancyStats;
import com.proveritus.propertyservice.entity.Floor;
import com.proveritus.propertyservice.entity.Property;
import com.proveritus.propertyservice.enums.OccupancyStatus;
import com.proveritus.cloudutility.exception.EntityNotFoundException;
import com.proveritus.propertyservice.repository.FloorRepository;
import com.proveritus.propertyservice.repository.PropertyRepository;
import com.proveritus.propertyservice.service.FloorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FloorServiceImpl implements FloorService {
    private final FloorRepository floorRepository;
    private final PropertyRepository propertyRepository;
    private final ModelMapper modelMapper;

    @Override
    public FloorDTO createFloor(FloorDTO floorDTO) {
        log.info("Creating new floor: {}", floorDTO.getName());
        validateFloor(floorDTO);

        Property property = validatePropertyExists(floorDTO.getPropertyId());
        Floor floor = modelMapper.map(floorDTO, Floor.class);
        floor.setProperty(property);

        Floor savedFloor = floorRepository.save(floor);
        log.debug("Floor created successfully with ID: {}", savedFloor.getId());

        return modelMapper.map(savedFloor, FloorDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public FloorDTO getFloorById(Long id) {
        log.debug("Fetching floor with ID: {}", id);
        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Floor not found with ID: {}", id);
                    return new EntityNotFoundException("Floor not found with id: " + id);
                });
        return modelMapper.map(floor, FloorDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FloorDTO> getFloorsByPropertyId(Long propertyId) {
        log.debug("Fetching all floors for property ID: {}", propertyId);
        validatePropertyExists(propertyId);
        return floorRepository.findByPropertyId(propertyId).stream()
                .map(floor -> modelMapper.map(floor, FloorDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FloorDTO> getFloorsByPropertyId(Long propertyId, Pageable pageable) {
        log.debug("Fetching paginated floors for property ID: {}", propertyId);
        validatePropertyExists(propertyId);
        return floorRepository.findByPropertyId(propertyId, pageable)
                .map(floor -> modelMapper.map(floor, FloorDTO.class));
    }

    @Override
    public FloorDTO updateFloor(Long id, FloorDTO floorDTO) {
        log.info("Updating floor with ID: {}", id);

        Floor existingFloor = floorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Floor not found with id: " + id));

        Property property = validatePropertyExists(floorDTO.getPropertyId());

        if (!existingFloor.getProperty().getId().equals(property.getId())) {
            existingFloor.setProperty(property);
        }

        if (!existingFloor.getName().equals(floorDTO.getName())) {
            checkForDuplicateFloorName(floorDTO.getName(), property.getId());
        }

        updateFloorFields(existingFloor, floorDTO);
        Floor updatedFloor = floorRepository.save(existingFloor);

        log.debug("Floor updated successfully with ID: {}", id);
        return modelMapper.map(updatedFloor, FloorDTO.class);
    }

    @Override
    public void deleteFloor(Long id) {
        log.info("Deleting floor with ID: {}", id);
        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Floor not found with id: " + id));

        if (!floor.getUnits().isEmpty()) {
            log.error("Cannot delete floor with ID: {} as it has {} units", id, floor.getUnits().size());
            throw new IllegalStateException("Cannot delete floor with existing units. Please remove units first.");
        }

        floorRepository.deleteById(id);
        log.debug("Floor deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public FloorOccupancyStats getFloorOccupancyStats(Long id) {
        log.debug("Fetching occupancy stats for floor ID: {}", id);
        val floor = floorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Floor not found with id: " + id));

        int totalUnits = floor.getUnits().size();
        int occupiedUnits = (int) floor.getUnits().stream()
                .filter(unit -> unit.getOccupancyStatus() == OccupancyStatus.OCCUPIED)
                .count();
        int vacantUnits = (int) floor.getUnits().stream()
                .filter(unit -> unit.getOccupancyStatus() == OccupancyStatus.AVAILABLE)
                .count();
        int reservedUnits = (int) floor.getUnits().stream()
                .filter(unit -> unit.getOccupancyStatus() == OccupancyStatus.RESERVED)
                .count();
        int notAvailableUnits = (int) floor.getUnits().stream()
                .filter(unit -> unit.getOccupancyStatus() == OccupancyStatus.NOT_AVAILABLE)
                .count();
        int underMaintenanceUnits = (int) floor.getUnits().stream()
                .filter(unit -> unit.getOccupancyStatus() == OccupancyStatus.UNDER_MAINTENANCE)
                .count();

        double occupancyRate = totalUnits > 0 ? (occupiedUnits * 100.0) / totalUnits : 0;
        double vacancyRate = totalUnits > 0 ? (vacantUnits * 100.0) / totalUnits : 0;
        double reservedRate = totalUnits > 0 ? (reservedUnits * 100.0) / totalUnits : 0;
        double notAvailableRate = totalUnits > 0 ? (notAvailableUnits * 100.0) / totalUnits : 0;
        double underMaintenanceRate = totalUnits > 0 ? (underMaintenanceUnits * 100.0) / totalUnits : 0;

        return new FloorOccupancyStats(totalUnits, occupiedUnits, vacantUnits, reservedUnits,
                notAvailableUnits, underMaintenanceUnits, occupancyRate, vacancyRate,
                reservedRate, notAvailableRate, underMaintenanceRate);
    }

    @Override
    public void updateFloorOccupancyStats(Long floorId) {
        log.debug("Updating occupancy stats for floor ID: {}", floorId);
        Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() -> new EntityNotFoundException("Floor not found with id: " + floorId));

        int totalUnits = floor.getUnits().size();
        int occupiedUnits = (int) floor.getUnits().stream()
                .filter(unit -> unit.getOccupancyStatus() == OccupancyStatus.OCCUPIED)
                .count();
        int vacantUnits = (int) floor.getUnits().stream()
                .filter(unit -> unit.getOccupancyStatus() == OccupancyStatus.AVAILABLE)
                .count();

        floor.setNumberOfUnits(totalUnits);
        floor.setOccupiedUnits(occupiedUnits);
        floor.setVacantUnits(vacantUnits);

        floorRepository.save(floor);
        log.debug("Updated occupancy stats for floor ID: {}", floorId);
    }

    private void checkForDuplicateFloorName(String name, Long propertyId) {
        if (floorRepository.findByNameAndPropertyId(name, propertyId).isPresent()) {
            log.error("Floor with name '{}' already exists in property ID: {}", name, propertyId);
            throw new IllegalArgumentException(
                    String.format("Floor with name '%s' already exists in property with id: %d", name, propertyId)
            );
        }
    }

    private void updateFloorFields(Floor existingFloor, FloorDTO floorDTO) {
        existingFloor.setName(floorDTO.getName());
        existingFloor.setNumberOfUnits(floorDTO.getNumberOfUnits());
        existingFloor.setOccupiedUnits(floorDTO.getOccupiedUnits());
        existingFloor.setVacantUnits(floorDTO.getVacantUnits());
    }

    private void validateFloor(FloorDTO floorDTO) {
        if (floorRepository.findByNameAndPropertyId(floorDTO.getName(), floorDTO.getPropertyId()).isPresent()) {
            log.error("Floor with name {} already exists in property ID: {}",
                    floorDTO.getName(), floorDTO.getPropertyId());
            throw new IllegalArgumentException("Floor with name " + floorDTO.getName() +
                    " already exists in property with id: " + floorDTO.getPropertyId());
        }

        if (floorDTO.getName() == null || floorDTO.getName().trim().isEmpty()) {
            log.error("Floor name cannot be empty");
            throw new IllegalArgumentException("Floor name cannot be empty");
        }
    }

    private Property validatePropertyExists(Long propertyId) {
        if (propertyId == null) {
            throw new IllegalArgumentException("Property ID must not be null");
        }

        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> {
                    log.error("Property not found with ID: {}", propertyId);
                    return new EntityNotFoundException("Property not found with id: " + propertyId);
                });
    }
}
