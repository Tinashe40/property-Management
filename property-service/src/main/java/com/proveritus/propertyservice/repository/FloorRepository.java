package com.proveritus.propertyservice.repository;

import com.proveritus.cloudutility.jpa.BaseDao;
import com.proveritus.propertyservice.entity.Floor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;

@Repository
public interface FloorRepository extends BaseDao<Floor, Long> {
    List<Floor> findByPropertyId(Long propertyId);
    Page<Floor> findByPropertyId(Long propertyId, Pageable pageable);
    Optional<Floor> findByNameAndPropertyId(String name, Long propertyId);

    @Query("SELECT COUNT(f) FROM Floor f WHERE f.property.id = :propertyId")
    long countByPropertyId(@Param("propertyId") Long propertyId);
}
