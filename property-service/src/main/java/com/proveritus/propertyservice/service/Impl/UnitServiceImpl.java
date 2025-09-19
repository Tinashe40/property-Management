package com.proveritus.propertyservice.service.Impl;

import com.proveritus.propertyservice.dto.UnitDTO;
import com.proveritus.propertyservice.entity.Floor;
import com.proveritus.propertyservice.entity.Property;
import com.proveritus.propertyservice.entity.Unit;
import com.proveritus.propertyservice.enums.OccupancyStatus;
import com.proveritus.propertyservice.enums.RentType;
import com.proveritus.cloudutility.exception.EntityNotFoundException;
import com.proveritus.propertyservice.repository.FloorRepository;
import com.proveritus.propertyservice.repository.PropertyRepository;
import com.proveritus.propertyservice.repository.UnitRepository;
import com.proveritus.propertyservice.service.FloorService;
import com.proveritus.propertyservice.service.UnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class UnitServiceImpl implements UnitService {
    private final UnitRepository unitRepository;
    private final PropertyRepository propertyRepository;
    private final FloorRepository floorRepository;
    private final ModelMapper modelMapper;
    private final FloorService floorService;

    @Override
    public UnitDTO createUnit(UnitDTO unitDTO) {
        log.info("Creating new unit: {}", unitDTO.getName());
        validateUnit(unitDTO, null);

        Property property = getPropertyById(unitDTO.getPropertyId());
        Floor floor = getFloorIfProvided(unitDTO.getFloorId());

        calculateMonthlyRent(unitDTO);

        Unit unit = modelMapper.map(unitDTO, Unit.class);
        unit.setProperty(property);
        unit.setFloor(floor);

        Unit savedUnit = unitRepository.save(unit);
        updateFloorOccupancyIfNeeded(unitDTO.getFloorId());

        log.debug("Unit created successfully with ID: {}", savedUnit.getId());
        return modelMapper.map(savedUnit, UnitDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UnitDTO getUnitById(Long id) {
        log.debug("Fetching unit with ID: {}", id);
        return modelMapper.map(findUnitById(id), UnitDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public UnitDTO getUnitByNameAndPropertyId(String name, Long propertyId) {
        log.debug("Fetching unit with name: {} in property ID: {}", name, propertyId);
        Unit unit = unitRepository.findByNameAndPropertyId(name, propertyId)
                .orElseThrow(() -> {
                    log.error("Unit not found with name: {} in property ID: {}", name, propertyId);
                    return new EntityNotFoundException("Unit not found with name: " + name + " in property ID: " + propertyId);
                });
        return modelMapper.map(unit, UnitDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnitDTO> getUnitsByPropertyId(Long propertyId) {
        log.debug("Fetching all units for property ID: {}", propertyId);
        validatePropertyExists(propertyId);
        return unitRepository.findByPropertyId(propertyId).stream()
                .map(unit -> modelMapper.map(unit, UnitDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UnitDTO> getUnitsByPropertyId(Long propertyId, Pageable pageable) {
        log.debug("Fetching paginated units for property ID: {}", propertyId);
        validatePropertyExists(propertyId);
        return unitRepository.findByPropertyId(propertyId, pageable)
                .map(unit -> modelMapper.map(unit, UnitDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnitDTO> getUnitsByFloorId(Long floorId) {
        log.debug("Fetching all units for floor ID: {}", floorId);
        validateFloorExists(floorId);
        return unitRepository.findByFloorId(floorId).stream()
                .map(unit -> modelMapper.map(unit, UnitDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UnitDTO> getUnitsByFloorId(Long floorId, Pageable pageable) {
        log.debug("Fetching paginated units for floor ID: {}", floorId);
        validateFloorExists(floorId);
        return unitRepository.findByFloorId(floorId, pageable)
                .map(unit -> modelMapper.map(unit, UnitDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UnitDTO> getUnitsWithFilters(Long propertyId, Long floorId, OccupancyStatus occupancyStatus, Pageable pageable) {
        log.debug("Fetching units with filters - Property ID: {}, Floor ID: {}, Occupancy Status: {}",
                propertyId, floorId, occupancyStatus);

        if (propertyId != null) validatePropertyExists(propertyId);
        if (floorId != null) validateFloorExists(floorId);

        return unitRepository.findWithFilters(propertyId, floorId, occupancyStatus, pageable)
                .map(unit -> modelMapper.map(unit, UnitDTO.class));
    }

    @Override
    public UnitDTO updateUnit(Long id, UnitDTO unitDTO) {
        log.info("Updating unit with ID: {}", id);
        Unit existingUnit = findUnitById(id);
        validateUnit(unitDTO, id);

        Property property = getPropertyById(unitDTO.getPropertyId());
        Floor floor = getFloorIfProvided(unitDTO.getFloorId());

        calculateMonthlyRent(unitDTO);

        modelMapper.map(unitDTO, existingUnit);
        existingUnit.setProperty(property);
        existingUnit.setFloor(floor);

        Unit updatedUnit = unitRepository.save(existingUnit);
        updateFloorOccupancyIfNeeded(unitDTO.getFloorId());

        log.debug("Unit updated successfully with ID: {}", id);
        return modelMapper.map(updatedUnit, UnitDTO.class);
    }

    @Override
    public void deleteUnit(Long id) {
        log.info("Deleting unit with ID: {}", id);
        Unit unit = findUnitById(id);
        Long floorId = (unit.getFloor() != null) ? unit.getFloor().getId() : null;

        unitRepository.deleteById(id);
        updateFloorOccupancyIfNeeded(floorId);

        log.debug("Unit deleted successfully with ID: {}", id);
    }

    @Override
    public UnitDTO updateOccupancyStatus(Long id, OccupancyStatus occupancyStatus, String tenant) {
        log.info("Updating occupancy status for unit ID: {} to {}", id, occupancyStatus);
        Unit unit = findUnitById(id);

        unit.setOccupancyStatus(occupancyStatus);
        unit.setTenant(OccupancyStatus.OCCUPIED.equals(occupancyStatus) ? tenant : null);

        Unit updatedUnit = unitRepository.save(unit);
        updateFloorOccupancyIfNeeded(unit.getFloor() != null ? unit.getFloor().getId() : null);

        log.debug("Occupancy status updated successfully for unit ID: {}", id);
        return modelMapper.map(updatedUnit, UnitDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnitDTO> searchUnits(String query) {
        log.debug("Searching units by query: {}", query);
        return unitRepository.searchUnits(query, Pageable.unpaged()).getContent().stream()
                .map(unit -> modelMapper.map(unit, UnitDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UnitDTO> searchUnits(String query, Pageable pageable) {
        log.debug("Searching units by query with pagination: {}", query);
        return unitRepository.searchUnits(query, pageable)
                .map(unit -> modelMapper.map(unit, UnitDTO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculatePotentialRentalIncome(Long propertyId) {
        log.debug("Calculating potential rental income for property ID: {}", propertyId);
        validatePropertyExists(propertyId);
        return unitRepository.calculateTotalRentalIncome(propertyId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnitsByPropertyId(Long propertyId) {
        log.debug("Counting units for property ID: {}", propertyId);
        validatePropertyExists(propertyId);
        return unitRepository.countByPropertyId(propertyId);
    }

    private void calculateMonthlyRent(UnitDTO unitDTO) {
        if (unitDTO.getRentType() == RentType.PSM &&
                unitDTO.getRatePerSqm() != null &&
                unitDTO.getSize() != null) {
            unitDTO.setMonthlyRent(unitDTO.getRatePerSqm() * unitDTO.getSize());
        }
    }

    private void validateUnit(UnitDTO unitDTO, Long excludedUnitId) {
        unitRepository.findByNameAndPropertyId(unitDTO.getName(), unitDTO.getPropertyId())
                .ifPresent(existingUnit -> {
                    if (!existingUnit.getId().equals(excludedUnitId)) {
                        log.error("Unit with name {} already exists in property ID: {}",
                                unitDTO.getName(), unitDTO.getPropertyId());
                        throw new IllegalArgumentException("Unit with name " + unitDTO.getName() +
                                " already exists in property with id: " + unitDTO.getPropertyId());
                    }
                });

        // Basic validation
        if (unitDTO.getName() == null || unitDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Unit name cannot be empty");
        }
        if (unitDTO.getSize() != null && unitDTO.getSize() <= 0) {
            throw new IllegalArgumentException("Unit size must be greater than 0");
        }
        if (unitDTO.getMonthlyRent() != null && unitDTO.getMonthlyRent() < 0) {
            throw new IllegalArgumentException("Monthly rent cannot be negative");
        }
        if (unitDTO.getRentType() == RentType.PSM &&
                unitDTO.getRatePerSqm() != null &&
                unitDTO.getRatePerSqm() < 0) {
            throw new IllegalArgumentException("Rate per square meter cannot be negative");
        }

        // Validate property existence
        if (!propertyRepository.existsById(unitDTO.getPropertyId())) {
            throw new EntityNotFoundException("Property not found with id: " + unitDTO.getPropertyId());
        }

        // Validate floor existence if provided
        if (unitDTO.getFloorId() != null && !floorRepository.existsById(unitDTO.getFloorId())) {
            throw new EntityNotFoundException("Floor not found with id: " + unitDTO.getFloorId());
        }
    }

    private Unit findUnitById(Long id) {
        return unitRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Unit not found with ID: {}", id);
                    return new EntityNotFoundException("Unit not found with id: " + id);
                });
    }

    private Property getPropertyById(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found with id: " + propertyId));
    }

    private Floor getFloorIfProvided(Long floorId) {
        if (floorId == null) return null;
        return floorRepository.findById(floorId)
                .orElseThrow(() -> new EntityNotFoundException("Floor not found with id: " + floorId));
    }

    private void updateFloorOccupancyIfNeeded(Long floorId) {
        if (floorId != null) {
            floorService.updateFloorOccupancyStats(floorId);
        }
    }

    private void validatePropertyExists(Long propertyId) {
        if (!propertyRepository.existsById(propertyId)) {
            log.error("Property not found with ID: {}", propertyId);
            throw new EntityNotFoundException("Property not found with id: " + propertyId);
        }
    }

    private void validateFloorExists(Long floorId) {
        if (!floorRepository.existsById(floorId)) {
            log.error("Floor not found with ID: {}", floorId);
            throw new EntityNotFoundException("Floor not found with id: " + floorId);
        }
    }
}
