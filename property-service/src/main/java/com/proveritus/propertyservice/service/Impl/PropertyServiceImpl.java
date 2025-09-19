package com.proveritus.propertyservice.service.Impl;

import com.proveritus.cloudutility.dto.UserDTO;
import com.proveritus.propertyservice.client.UserClient;
import com.proveritus.propertyservice.dto.PropertyDTO;
import com.proveritus.propertyservice.dto.PropertyStatsDTO;
import com.proveritus.propertyservice.entity.Property;
import com.proveritus.propertyservice.enums.PropertyType;
import com.proveritus.cloudutility.exception.EntityNotFoundException;
import com.proveritus.propertyservice.repository.PropertyRepository;
import com.proveritus.propertyservice.service.PropertyService;
import com.proveritus.propertyservice.validator.PropertyValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proveritus.cloudutility.exception.UserServiceNotAvailableException;
import feign.FeignException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PropertyServiceImpl implements PropertyService {
    private final PropertyRepository propertyRepository;
    private final ModelMapper modelMapper;
    private final UserClient userClient;
    private final PropertyValidator propertyValidator;

    @Override
    public PropertyDTO createProperty(PropertyDTO propertyDTO) {
        log.info("Creating new property: {}", propertyDTO.getName());
        propertyValidator.validate(propertyDTO);

        Property property = modelMapper.map(propertyDTO, Property.class);

        // Initialize collections to avoid null pointers
        if (property.getFloors() == null) {
            property.setFloors(new ArrayList<>());
        }
        if (property.getUnits() == null) {
            property.setUnits(new ArrayList<>());
        }

        Property savedProperty = propertyRepository.save(property);

        log.debug("Property created successfully with ID: {}", savedProperty.getId());
        return convertToDto(savedProperty);
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyDTO getPropertyById(Long id) {
        log.debug("Fetching property with ID: {}", id);
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Property not found with id: " + id));
        return convertToDto(property);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyDTO> getAllProperties(Pageable pageable) {
        log.debug("Fetching paginated properties");
        Page<Property> properties = propertyRepository.findAll(pageable);
        return enrichPropertiesWithUserDetails(properties);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<PropertyDTO> getAllPropertiesByType(PropertyType propertyType, Pageable pageable) {
        log.debug("Fetching properties of type: {}", propertyType);
        Page<Property> properties = propertyRepository.findByPropertyType(propertyType, pageable);
        return enrichPropertiesWithUserDetails(properties);
    }

    @Override
    public PropertyDTO updateProperty(Long id, PropertyDTO propertyDTO) {
        log.info("Updating property with ID: {}", id);
        Property existingProperty = propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Property not found with id: " + id));

        // Check if name is being changed and if new name already exists
        if (!existingProperty.getName().equals(propertyDTO.getName()) &&
                propertyRepository.existsByName(propertyDTO.getName())) {
            throw new IllegalArgumentException("Property with name " + propertyDTO.getName() + " already exists");
        }

        // Preserve the ID and only update allowed fields
        modelMapper.map(propertyDTO, existingProperty);
        existingProperty.setId(id);

        Property updatedProperty = propertyRepository.save(existingProperty);
        log.debug("Property updated successfully with ID: {}", id);
        return convertToDto(updatedProperty);
    }

    @Override
    public void deleteProperty(Long id) {
        log.info("Deleting property with ID: {}", id);
        if (!propertyRepository.existsById(id)) {
            log.error("Property ID: {} was not found", id);
            throw new EntityNotFoundException("Property not found with id: " + id);
        }

        // Check if property has floors or units before deletion
        Property property = propertyRepository.findById(id).get();
        if (!property.getFloors().isEmpty() || !property.getUnits().isEmpty()) {
            log.error("Cannot delete property with ID: {} as it has floors or units", id);
            throw new IllegalStateException("Cannot delete property with existing floors or units. Please remove them first.");
        }

        propertyRepository.deleteById(id);
        log.debug("Property deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyDTO> searchProperties(String query, Pageable pageable) {
        log.debug("Searching properties by query: {}", query);
        Page<Property> properties = propertyRepository.searchProperties(query, pageable);
        return enrichPropertiesWithUserDetails(properties);
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyStatsDTO getPropertyStats(Long id) {
        log.debug("Fetching stats for property ID: {}", id);
        return propertyRepository.getPropertyStats(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalPropertiesCount() {
        log.debug("Fetching total properties count");
        return propertyRepository.countAllProperties();
    }

    private PropertyDTO convertToDto(Property property) {
        PropertyDTO propertyDTO = modelMapper.map(property, PropertyDTO.class);
        enrichPropertyDTOWithUserDetails(property, propertyDTO);
        return propertyDTO;
    }

    private void enrichPropertyDTOWithUserDetails(Property property, PropertyDTO propertyDTO) {
        if (property.getManagedBy() != null) {
            try {
                UserDTO userDTO = userClient.getUserById(property.getManagedBy());
                propertyDTO.setManagedByDetails(userDTO);
            } catch (FeignException e) {
                log.error("Unable to fetch user details for user id: {}", property.getManagedBy(), e);
                throw new UserServiceNotAvailableException("User service is currently unavailable. Please try again later.");
            }
        }
    }

    private Page<PropertyDTO> enrichPropertiesWithUserDetails(Page<Property> properties) {
        List<Long> userIds = properties.getContent().stream()
                .map(Property::getManagedBy)
                .collect(Collectors.toList());

        if (userIds.isEmpty()) {
            return properties.map(this::convertToDto);
        }

        try {
            List<UserDTO> userDTOs = userClient.getUsersByIds(userIds);
            Map<Long, UserDTO> userMap = userDTOs.stream()
                    .collect(Collectors.toMap(UserDTO::getId, user -> user));

            return properties.map(property -> {
                PropertyDTO dto = modelMapper.map(property, PropertyDTO.class);
                if (property.getManagedBy() != null) {
                    dto.setManagedByDetails(userMap.get(property.getManagedBy()));
                }
                return dto;
            });
        } catch (FeignException e) {
            log.error("Unable to fetch user details for user ids: {}", userIds, e);
            throw new UserServiceNotAvailableException("User service is currently unavailable. Please try again later.");
        }
    }
}
