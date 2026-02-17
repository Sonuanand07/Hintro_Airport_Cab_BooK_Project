package com.hintro.airportpooling.dto;

import com.hintro.airportpooling.entity.RideDirection;
import com.hintro.airportpooling.entity.RideGroupStatus;

import java.util.List;

public class RideGroupResponse {
    private Long id;
    private RideDirection direction;
    private RideGroupStatus status;
    private int totalSeats;
    private int totalLuggage;
    private int maxSeats;
    private int maxLuggage;
    private double routeDistanceKm;
    private Long cabId;
    private List<RideGroupMemberResponse> members;

    public RideGroupResponse(Long id,
                             RideDirection direction,
                             RideGroupStatus status,
                             int totalSeats,
                             int totalLuggage,
                             int maxSeats,
                             int maxLuggage,
                             double routeDistanceKm,
                             Long cabId,
                             List<RideGroupMemberResponse> members) {
        this.id = id;
        this.direction = direction;
        this.status = status;
        this.totalSeats = totalSeats;
        this.totalLuggage = totalLuggage;
        this.maxSeats = maxSeats;
        this.maxLuggage = maxLuggage;
        this.routeDistanceKm = routeDistanceKm;
        this.cabId = cabId;
        this.members = members;
    }

    public Long getId() {
        return id;
    }

    public RideDirection getDirection() {
        return direction;
    }

    public RideGroupStatus getStatus() {
        return status;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public int getTotalLuggage() {
        return totalLuggage;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public int getMaxLuggage() {
        return maxLuggage;
    }

    public double getRouteDistanceKm() {
        return routeDistanceKm;
    }

    public Long getCabId() {
        return cabId;
    }

    public List<RideGroupMemberResponse> getMembers() {
        return members;
    }
}
