package com.hintro.airportpooling.dto;

import com.hintro.airportpooling.entity.RideDirection;
import com.hintro.airportpooling.entity.RideRequestStatus;

import java.time.Instant;

public class RideRequestResponse {
    private Long id;
    private Long passengerId;
    private RideDirection direction;
    private RideRequestStatus status;
    private Long assignedGroupId;
    private double fareEstimate;
    private double maxDetourPct;
    private Instant desiredPickupTime;

    public RideRequestResponse(Long id,
                               Long passengerId,
                               RideDirection direction,
                               RideRequestStatus status,
                               Long assignedGroupId,
                               double fareEstimate,
                               double maxDetourPct,
                               Instant desiredPickupTime) {
        this.id = id;
        this.passengerId = passengerId;
        this.direction = direction;
        this.status = status;
        this.assignedGroupId = assignedGroupId;
        this.fareEstimate = fareEstimate;
        this.maxDetourPct = maxDetourPct;
        this.desiredPickupTime = desiredPickupTime;
    }

    public Long getId() {
        return id;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public RideDirection getDirection() {
        return direction;
    }

    public RideRequestStatus getStatus() {
        return status;
    }

    public Long getAssignedGroupId() {
        return assignedGroupId;
    }

    public double getFareEstimate() {
        return fareEstimate;
    }

    public double getMaxDetourPct() {
        return maxDetourPct;
    }

    public Instant getDesiredPickupTime() {
        return desiredPickupTime;
    }
}
