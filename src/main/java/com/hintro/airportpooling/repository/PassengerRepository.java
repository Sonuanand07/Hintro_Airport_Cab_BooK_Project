package com.hintro.airportpooling.repository;

import com.hintro.airportpooling.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    Optional<Passenger> findByPhone(String phone);
}
