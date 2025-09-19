package com.proveritus.propertyservice.repository;

import com.proveritus.cloudutility.jpa.BaseDao;
import com.proveritus.propertyservice.entity.Unit;
import com.proveritus.propertyservice.enums.OccupancyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitRepository extends BaseDao<Unit, Long> {
    List<Unit> findByPropertyId(Long propertyId);

    List<Unit> findByFloorId(Long floorId);

    Optional<Unit> findByNameAndPropertyId(String name, Long propertyId);

    Page<Unit> findByPropertyId(Long propertyId, Pageable pageable);

    Page<Unit> findByFloorId(Long floorId, Pageable pageable);

    long countByFloorId(Long floorId);

    @Query("SELECT COUNT(u) FROM Unit u WHERE u.floor.id = :floorId AND u.occupancyStatus = :occupancyStatus")
    long countByFloorIdAndOccupancy(@Param("floorId") Long floorId, @Param("occupancy") OccupancyStatus occupancyStatus);

    List<Unit> findByOccupancyStatus(OccupancyStatus occupancyStatus);

    Page<Unit> findByOccupancyStatus(OccupancyStatus occupancyStatus, Pageable pageable);

    @Query("SELECT u FROM Unit u WHERE " +
            "(:propertyId IS NULL OR u.property.id = :propertyId) AND " +
            "(:floorId IS NULL OR u.floor.id = :floorId) AND " +
            "(:occupancyStatus IS NULL OR u.occupancyStatus = :occupancyStatus)")
    Page<Unit> findWithFilters(@Param("propertyId") Long propertyId,
                               @Param("floorId") Long floorId,
                               @Param("occupancy") OccupancyStatus occupancyStatus,
                               Pageable pageable);

    @Query("SELECT u FROM Unit u WHERE " + "LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " + "LOWER(u.tenant) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Unit> searchUnits(@Param("query") String query, Pageable pageable);

    @Query("SELECT COALESCE(SUM(u.monthlyRent), 0) FROM Unit u WHERE u.property.id = :propertyId AND u.occupancyStatus = 'OCCUPIED'")
    Double calculateTotalRentalIncome(@Param("propertyId") Long propertyId);

    @Query("SELECT COUNT(u) FROM Unit u WHERE u.property.id = :propertyId")
    long countByPropertyId(@Param("propertyId") Long propertyId);

}
