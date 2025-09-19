package com.proveritus.propertyservice.dto;

import com.proveritus.propertyservice.enums.OccupancyStatus;
import com.proveritus.propertyservice.enums.RentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitDTO {

    @NotBlank(message = "Unit name is required")
    private String name;

    @Positive(message = "Size must be positive")
    private Double size;

    private RentType rentType;

    @Positive(message = "Rate per square meter must be positive")
    private Double ratePerSqm;

    @Positive(message = "Monthly rent must be positive")
    private Double monthlyRent;

    private OccupancyStatus occupancyStatus;

    private String tenant;

    @NotNull(message = "Property ID is required")
    private Long propertyId;

    private Long floorId;
}
