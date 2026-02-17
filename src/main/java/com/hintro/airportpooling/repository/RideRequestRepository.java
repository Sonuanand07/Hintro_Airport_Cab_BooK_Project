package com.hintro.airportpooling.repository;

import com.hintro.airportpooling.entity.RideRequest;
import com.hintro.airportpooling.entity.RideRequestStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RideRequest r where r.status = :status and r.desiredPickupTime >= :fromTime order by r.createdAt")
    List<RideRequest> findPendingForUpdate(@Param("status") RideRequestStatus status,
                                           @Param("fromTime") Instant fromTime,
                                           Pageable pageable);

    List<RideRequest> findByAssignedGroupId(Long groupId);

    long countByStatus(RideRequestStatus status);
}
