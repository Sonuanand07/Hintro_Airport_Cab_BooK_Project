package com.hintro.airportpooling.service;

import com.hintro.airportpooling.entity.Passenger;
import com.hintro.airportpooling.repository.PassengerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PassengerService {
    private final PassengerRepository passengerRepository;

    public PassengerService(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    @Transactional
    public Passenger createPassenger(String name, String phone) {
        return passengerRepository.findByPhone(phone)
                .orElseGet(() -> {
                    Passenger passenger = new Passenger();
                    passenger.setName(name);
                    passenger.setPhone(phone);
                    return passengerRepository.save(passenger);
                });
    }

    public Passenger getPassenger(Long id) {
        return passengerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Passenger not found"));
    }
}
