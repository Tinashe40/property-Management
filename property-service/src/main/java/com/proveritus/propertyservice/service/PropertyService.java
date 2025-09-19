package com.proveritus.propertyservice.service;

import com.proveritus.propertyservice.dto.PropertyDTO;
import com.proveritus.propertyservice.dto.PropertyStatsDTO;
import com.proveritus.propertyservice.enums.PropertyType;
import com.proveritus.cloudutility.exception.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PropertyService {
    PropertyDTO createProperty(PropertyDTO propertyDTO);

    PropertyDTO getPropertyById(Long id) throws EntityNotFoundException;

    Page<PropertyDTO> getAllPropertiesByType(PropertyType propertyType, Pageable pageable);

    PropertyDTO updateProperty(Long id, PropertyDTO propertyDTO) throws EntityNotFoundException;

    void deleteProperty(Long id) throws EntityNotFoundException;

    Page<PropertyDTO> getAllProperties(Pageable pageable);

    Page<PropertyDTO> searchProperties(String query, Pageable pageable);

    PropertyStatsDTO getPropertyStats(Long id) throws EntityNotFoundException;

    long getTotalPropertiesCount();
}

