package com.proveritus.propertyservice.service;

import com.proveritus.propertyservice.dto.UnitDTO;
import com.proveritus.propertyservice.enums.OccupancyStatus;
import com.proveritus.cloudutility.exception.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UnitService {
    UnitDTO createUnit(UnitDTO unitDTO);

    UnitDTO getUnitById(Long id) throws EntityNotFoundException;

    UnitDTO getUnitByNameAndPropertyId(String name, Long propertyId) throws EntityNotFoundException;

    List<UnitDTO> getUnitsByPropertyId(Long propertyId);

    Page<UnitDTO> getUnitsByPropertyId(Long propertyId, Pageable pageable);

    List<UnitDTO> getUnitsByFloorId(Long floorId);

    Page<UnitDTO> getUnitsByFloorId(Long floorId, Pageable pageable);

    Page<UnitDTO> getUnitsWithFilters(Long propertyId, Long floorId, OccupancyStatus occupancyStatus, Pageable pageable);

    UnitDTO updateUnit(Long id, UnitDTO unitDTO) throws EntityNotFoundException;

    void deleteUnit(Long id) throws EntityNotFoundException;

    UnitDTO updateOccupancyStatus(Long id, OccupancyStatus occupancyStatus, String tenant) throws EntityNotFoundException;

    List<UnitDTO> searchUnits(String query);

    Page<UnitDTO> searchUnits(String query, Pageable pageable);

    Double calculatePotentialRentalIncome(Long propertyId);

    long countUnitsByPropertyId(Long propertyId);
}

