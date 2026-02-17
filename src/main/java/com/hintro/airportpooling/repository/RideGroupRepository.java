package com.hintro.airportpooling.repository;

import com.hintro.airportpooling.entity.RideDirection;
import com.hintro.airportpooling.entity.RideGroup;
import com.hintro.airportpooling.entity.RideGroupStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface RideGroupRepository extends JpaRepository<RideGroup, Long> {

    @Query("select g from RideGroup g where g.status = :status and g.direction = :direction and g.timeWindowStart <= :time and g.timeWindowEnd >= :time")
    List<RideGroup> findOpenGroups(@Param("status") RideGroupStatus status,
                                   @Param("direction") RideDirection direction,
                                   @Param("time") Instant time);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select g from RideGroup g where g.id = :id")
    RideGroup findForUpdate(@Param("id") Long id);
}
