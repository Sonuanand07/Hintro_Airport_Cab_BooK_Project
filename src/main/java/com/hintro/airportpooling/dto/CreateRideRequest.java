package com.hintro.airportpooling.dto;

import com.hintro.airportpooling.entity.RideDirection;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class CreateRideRequest {
    @NotNull
    private Long passengerId;

    @NotNull
    private RideDirection direction;

    @NotNull
    private Double pickupLat;

    @NotNull
    private Double pickupLng;

    @NotNull
    private Double dropoffLat;

    @NotNull
    private Double dropoffLng;

    @Min(1)
    private int seatsRequired;

    @Min(0)
    private int luggageUnits;

    @Min(0)
    @Max(100)
    private Double maxDetourPct;

    @NotNull
    private Instant desiredPickupTime;

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public RideDirection getDirection() {
        return direction;
    }

    public void setDirection(RideDirection direction) {
        this.direction = direction;
    }

    public Double getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(Double pickupLat) {
        this.pickupLat = pickupLat;
    }

    public Double getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(Double pickupLng) {
        this.pickupLng = pickupLng;
    }

    public Double getDropoffLat() {
        return dropoffLat;
    }

    public void setDropoffLat(Double dropoffLat) {
        this.dropoffLat = dropoffLat;
    }

    public Double getDropoffLng() {
        return dropoffLng;
    }

    public void setDropoffLng(Double dropoffLng) {
        this.dropoffLng = dropoffLng;
    }

    public int getSeatsRequired() {
        return seatsRequired;
    }

    public void setSeatsRequired(int seatsRequired) {
        this.seatsRequired = seatsRequired;
    }

    public int getLuggageUnits() {
        return luggageUnits;
    }

    public void setLuggageUnits(int luggageUnits) {
        this.luggageUnits = luggageUnits;
    }

    public Double getMaxDetourPct() {
        return maxDetourPct;
    }

    public void setMaxDetourPct(Double maxDetourPct) {
        this.maxDetourPct = maxDetourPct;
    }

    public Instant getDesiredPickupTime() {
        return desiredPickupTime;
    }

    public void setDesiredPickupTime(Instant desiredPickupTime) {
        this.desiredPickupTime = desiredPickupTime;
    }
}
