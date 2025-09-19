package com.proveritus.propertyservice.entity;

import com.proveritus.propertyservice.entity.BaseEntity;
import com.proveritus.propertyservice.dto.PropertyStatsDTO;
import com.proveritus.propertyservice.enums.PropertyType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType propertyType;

    @Column(nullable = false)
    private String address;

    private Integer numberOfFloors;
    private Integer numberOfUnits;

    private Long managedBy;

    @Builder.Default
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Floor> floors = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Unit> units = new ArrayList<>();

    
}
