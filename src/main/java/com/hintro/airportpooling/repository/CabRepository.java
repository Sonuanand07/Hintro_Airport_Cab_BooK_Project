package com.hintro.airportpooling.repository;

import com.hintro.airportpooling.entity.Cab;
import com.hintro.airportpooling.entity.CabStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CabRepository extends JpaRepository<Cab, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cab c where c.status = :status and c.seatCapacity >= :seats and c.luggageCapacity >= :luggage order by c.id")
    List<Cab> findAvailableForUpdate(@Param("status") CabStatus status,
                                    @Param("seats") int seats,
                                    @Param("luggage") int luggage);

    long countByStatus(CabStatus status);
}
