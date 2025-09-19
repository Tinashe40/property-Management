package com.proveritus.propertyservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyStatsDTO {
    private long totalFloors;
    private long totalUnits;
    private long occupiedUnits;
    private long vacantUnits;
    private long reservedUnits;
    private long notAvailableUnits;
    private long underMaintenanceUnits;
    private double occupancyRate;
    private double vacancyRate;
    private double totalRentalIncome;
    private double potentialRentalIncome;
}