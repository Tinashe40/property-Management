package com.proveritus.propertyservice.repository;

import com.proveritus.cloudutility.jpa.BaseDao;
import com.proveritus.propertyservice.dto.PropertyStatsDTO;
import com.proveritus.propertyservice.entity.Property;
import com.proveritus.propertyservice.enums.PropertyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends BaseDao<Property, Long> {
    Optional<Property> findByName(String name);

    List<Property> findByNameContainingIgnoreCase(String name);

    Page<Property> findByNameContainingIgnoreCase(String name, Pageable pageable);

//   List<Property> findByPropertyType(PropertyType propertyType);

    Page<Property> findByPropertyType(PropertyType propertyType, Pageable pageable);

    @Query("SELECT p FROM Property p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.address) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Property> searchProperties(@Param("query") String query, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Property p")
    long countAllProperties();

    boolean existsByName(String name);

    @Query("""
            SELECT new com.proveritus.propertyservice.dto.PropertyStatsDTO(
                size(p.floors),
                size(p.units),
                SUM(CASE WHEN u.occupancyStatus = com.proveritus.propertyservice.enums.OccupancyStatus.OCCUPIED THEN 1 ELSE 0 END),
                SUM(CASE WHEN u.occupancyStatus = com.proveritus.propertyservice.enums.OccupancyStatus.AVAILABLE THEN 1 ELSE 0 END),
                SUM(CASE WHEN u.occupancyStatus = com.proveritus.propertyservice.enums.OccupancyStatus.RESERVED THEN 1 ELSE 0 END),
                SUM(CASE WHEN u.occupancyStatus = com.proveritus.propertyservice.enums.OccupancyStatus.NOT_AVAILABLE THEN 1 ELSE 0 END),
                SUM(CASE WHEN u.occupancyStatus = com.proveritus.propertyservice.enums.OccupancyStatus.UNDER_MAINTENANCE THEN 1 ELSE 0 END),
                (SUM(CASE WHEN u.occupancyStatus = com.proveritus.propertyservice.enums.OccupancyStatus.OCCUPIED THEN 1 ELSE 0 END) * 100.0) / size(p.units),
                (SUM(CASE WHEN u.occupancyStatus = com.proveritus.propertyservice.enums.OccupancyStatus.AVAILABLE THEN 1 ELSE 0 END) * 100.0) / size(p.units),
                SUM(CASE WHEN u.occupancyStatus = com.proveritus.propertyservice.enums.OccupancyStatus.OCCUPIED THEN u.monthlyRent ELSE 0 END),
                SUM(u.monthlyRent)
            )
            FROM Property p
            LEFT JOIN p.units u
            WHERE p.id = :id
            GROUP BY p
            """)
    PropertyStatsDTO getPropertyStats(@Param("id") Long id);
}
