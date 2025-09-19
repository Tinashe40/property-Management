package com.proveritus.propertyservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FloorOccupancyStats {

    private int totalUnits;
    private int occupiedUnits;
    private int vacantUnits;
    private int reservedUnits;
    private int notAvailableUnits;
    private int underMaintenanceUnits;
    private double occupancyRate;
    private double vacancyRate;
    private double reservedRate;
    private double notAvailableRate;
    private double underMaintenanceRate;
}
