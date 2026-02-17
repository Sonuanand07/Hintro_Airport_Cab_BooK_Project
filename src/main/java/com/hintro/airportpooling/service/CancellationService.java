package com.hintro.airportpooling.service;

import com.hintro.airportpooling.config.AppProperties;
import com.hintro.airportpooling.entity.RideGroup;
import com.hintro.airportpooling.entity.RideGroupMember;
import com.hintro.airportpooling.entity.RideGroupStatus;
import com.hintro.airportpooling.entity.RideRequest;
import com.hintro.airportpooling.entity.RideRequestStatus;
import com.hintro.airportpooling.repository.RideGroupMemberRepository;
import com.hintro.airportpooling.repository.RideGroupRepository;
import com.hintro.airportpooling.repository.RideRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CancellationService {
    private final RideRequestRepository rideRequestRepository;
    private final RideGroupRepository rideGroupRepository;
    private final RideGroupMemberRepository memberRepository;
    private final RideGroupService rideGroupService;
    private final CabService cabService;
    private final AppProperties properties;
    private final RoutePlanner routePlanner = new RoutePlanner();

    public CancellationService(RideRequestRepository rideRequestRepository,
                               RideGroupRepository rideGroupRepository,
                               RideGroupMemberRepository memberRepository,
                               RideGroupService rideGroupService,
                               CabService cabService,
                               AppProperties properties) {
        this.rideRequestRepository = rideRequestRepository;
        this.rideGroupRepository = rideGroupRepository;
        this.memberRepository = memberRepository;
        this.rideGroupService = rideGroupService;
        this.cabService = cabService;
        this.properties = properties;
    }

    @Transactional
    public RideRequest cancelRequest(Long requestId) {
        RideRequest request = rideRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (request.getStatus() == RideRequestStatus.CANCELLED || request.getStatus() == RideRequestStatus.COMPLETED) {
            return request;
        }
        request.setStatus(RideRequestStatus.CANCELLED);
        RideGroup group = request.getAssignedGroup();
        request.setAssignedGroup(null);
        rideRequestRepository.save(request);

        if (group == null) {
            return request;
        }

        RideGroup locked = rideGroupRepository.findForUpdate(group.getId());
        rideGroupService.removeMemberByRequest(requestId);
        List<RideGroupMember> remaining = memberRepository.findByRideGroupIdOrderByStopOrder(locked.getId());
        if (remaining.isEmpty()) {
            locked.setStatus(RideGroupStatus.CANCELLED);
            if (locked.getCab() != null) {
                cabService.releaseCab(locked.getCab());
                locked.setCab(null);
            }
            locked.setTotalSeats(0);
            locked.setTotalLuggage(0);
            locked.setRouteDistanceKm(0.0);
            rideGroupRepository.save(locked);
            return request;
        }

        RoutePlanner.RoutePlan plan = routePlanner.rebuildPlan(
                locked.getDirection(),
                properties.getAirport().getLat(),
                properties.getAirport().getLng(),
                remaining
        );
        if (plan != null) {
            locked.setTotalSeats(plan.getMembers().stream().mapToInt(p -> p.getRequest().getSeatsRequired()).sum());
            locked.setTotalLuggage(plan.getMembers().stream().mapToInt(p -> p.getRequest().getLuggageUnits()).sum());
            rideGroupRepository.save(locked);
            rideGroupService.applyPlan(locked, plan);
        }

        return request;
    }
}
