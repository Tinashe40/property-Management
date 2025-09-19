package com.proveritus.propertyservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FloorDTO {

    @NotNull(message = "Property ID is required")
    private Long propertyId;

    @NotBlank(message = "Floor name is required")
    private String name;

    private Integer numberOfUnits;
    private Integer occupiedUnits;
    private Integer vacantUnits;
    private List<UnitDTO> units;
}
