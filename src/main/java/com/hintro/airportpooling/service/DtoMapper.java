package com.hintro.airportpooling.service;

import com.hintro.airportpooling.dto.CabResponse;
import com.hintro.airportpooling.dto.PassengerResponse;
import com.hintro.airportpooling.dto.RideGroupMemberResponse;
import com.hintro.airportpooling.dto.RideGroupResponse;
import com.hintro.airportpooling.dto.RideRequestResponse;
import com.hintro.airportpooling.entity.Cab;
import com.hintro.airportpooling.entity.Passenger;
import com.hintro.airportpooling.entity.RideGroup;
import com.hintro.airportpooling.entity.RideGroupMember;
import com.hintro.airportpooling.entity.RideRequest;

import java.util.List;

public final class DtoMapper {
    private DtoMapper() {
    }

    public static PassengerResponse toPassengerResponse(Passenger passenger) {
        return new PassengerResponse(passenger.getId(), passenger.getName(), passenger.getPhone());
    }

    public static CabResponse toCabResponse(Cab cab) {
        return new CabResponse(cab.getId(), cab.getPlateNumber(), cab.getSeatCapacity(), cab.getLuggageCapacity(), cab.getStatus());
    }

    public static RideRequestResponse toRideRequestResponse(RideRequest request) {
        Long groupId = request.getAssignedGroup() == null ? null : request.getAssignedGroup().getId();
        return new RideRequestResponse(
                request.getId(),
                request.getPassenger().getId(),
                request.getDirection(),
                request.getStatus(),
                groupId,
                request.getFareEstimate(),
                request.getMaxDetourPct(),
                request.getDesiredPickupTime()
        );
    }

    public static RideGroupResponse toRideGroupResponse(RideGroup group, List<RideGroupMember> members) {
        Long cabId = group.getCab() == null ? null : group.getCab().getId();
        List<RideGroupMemberResponse> memberResponses = members.stream()
                .map(member -> new RideGroupMemberResponse(
                        member.getRideRequest().getId(),
                        member.getStopOrder(),
                        member.getDetourPct()))
                .toList();
        return new RideGroupResponse(
                group.getId(),
                group.getDirection(),
                group.getStatus(),
                group.getTotalSeats(),
                group.getTotalLuggage(),
                group.getMaxSeats(),
                group.getMaxLuggage(),
                group.getRouteDistanceKm(),
                cabId,
                memberResponses
        );
    }
}
