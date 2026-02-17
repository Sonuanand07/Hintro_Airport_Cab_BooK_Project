package com.hintro.airportpooling.dto;

import com.hintro.airportpooling.entity.CabStatus;

public class CabResponse {
    private Long id;
    private String plateNumber;
    private int seatCapacity;
    private int luggageCapacity;
    private CabStatus status;

    public CabResponse(Long id, String plateNumber, int seatCapacity, int luggageCapacity, CabStatus status) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.seatCapacity = seatCapacity;
        this.luggageCapacity = luggageCapacity;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public int getSeatCapacity() {
        return seatCapacity;
    }

    public int getLuggageCapacity() {
        return luggageCapacity;
    }

    public CabStatus getStatus() {
        return status;
    }
}
