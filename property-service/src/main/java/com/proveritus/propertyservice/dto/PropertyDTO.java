package com.proveritus.propertyservice.dto;

import com.proveritus.cloudutility.dto.UserDTO;
import com.proveritus.propertyservice.enums.PropertyType;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyDTO {

    private String name;
    private PropertyType propertyType;
    private String address;
    private Integer numberOfFloors;
    private Integer numberOfUnits;
    private Long managedBy;
    private UserDTO managedByDetails;
    private List<FloorDTO> floors;
    private List<UnitDTO> units;
}
