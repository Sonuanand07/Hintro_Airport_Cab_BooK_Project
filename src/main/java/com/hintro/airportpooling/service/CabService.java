package com.hintro.airportpooling.service;

import com.hintro.airportpooling.entity.Cab;
import com.hintro.airportpooling.entity.CabStatus;
import com.hintro.airportpooling.repository.CabRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CabService {
    private final CabRepository cabRepository;

    public CabService(CabRepository cabRepository) {
        this.cabRepository = cabRepository;
    }

    public Cab createCab(String plateNumber, int seatCapacity, int luggageCapacity) {
        Cab cab = new Cab();
        cab.setPlateNumber(plateNumber);
        cab.setSeatCapacity(seatCapacity);
        cab.setLuggageCapacity(luggageCapacity);
        cab.setStatus(CabStatus.AVAILABLE);
        return cabRepository.save(cab);
    }

    public List<Cab> listCabs() {
        return cabRepository.findAll();
    }

    @Transactional
    public Optional<Cab> allocateCab(int seats, int luggage) {
        List<Cab> available = cabRepository.findAvailableForUpdate(CabStatus.AVAILABLE, seats, luggage);
        if (available.isEmpty()) {
            return Optional.empty();
        }
        Cab cab = available.get(0);
        cab.setStatus(CabStatus.IN_SERVICE);
        cabRepository.save(cab);
        return Optional.of(cab);
    }

    @Transactional
    public void releaseCab(Cab cab) {
        cab.setStatus(CabStatus.AVAILABLE);
        cabRepository.save(cab);
    }
}
