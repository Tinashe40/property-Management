package com.proveritus.propertyservice.entity;

import com.proveritus.propertyservice.entity.BaseEntity;
import com.proveritus.propertyservice.enums.OccupancyStatus;
import com.proveritus.propertyservice.enums.RentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Entity
@Table(name = "units")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Unit extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Positive
    private Double size;

    @Enumerated(EnumType.STRING)
    private RentType rentType;

    @Positive
    private Double ratePerSqm;

    @Positive
    private Double monthlyRent;

    @Enumerated(EnumType.STRING)
    private OccupancyStatus occupancyStatus;

    private String tenant;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id")
    private Floor floor;
}
