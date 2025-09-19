package com.proveritus.propertyservice.validator;

import com.proveritus.propertyservice.dto.PropertyDTO;
import com.proveritus.propertyservice.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PropertyValidator {

    private final PropertyRepository propertyRepository;

    public void validate(PropertyDTO propertyDTO) {
        if (propertyRepository.findByName(propertyDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("Property with name " + propertyDTO.getName() + " already exists");
        }

        if (propertyDTO.getName() == null || propertyDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Property name cannot be empty");
        }

        if (propertyDTO.getAddress() == null || propertyDTO.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Property address cannot be empty");
        }

        if (propertyDTO.getPropertyType() == null) {
            throw new IllegalArgumentException("Property type cannot be null");
        }
    }
}
