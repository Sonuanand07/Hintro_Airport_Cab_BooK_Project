package com.hintro.airportpooling.service;

import com.hintro.airportpooling.config.AppProperties;
import com.hintro.airportpooling.entity.RideGroup;
import com.hintro.airportpooling.entity.RideGroupMember;
import com.hintro.airportpooling.entity.RideGroupStatus;
import com.hintro.airportpooling.entity.RideRequest;
import com.hintro.airportpooling.repository.RideGroupMemberRepository;
import com.hintro.airportpooling.repository.RideGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RideGroupService {
    private final RideGroupRepository rideGroupRepository;
    private final RideGroupMemberRepository memberRepository;
    private final AppProperties properties;

    public RideGroupService(RideGroupRepository rideGroupRepository,
                            RideGroupMemberRepository memberRepository,
                            AppProperties properties) {
        this.rideGroupRepository = rideGroupRepository;
        this.memberRepository = memberRepository;
        this.properties = properties;
    }

    @Transactional
    public RideGroup createGroup(RideRequest request) {
        RideGroup group = new RideGroup();
        group.setDirection(request.getDirection());
        group.setStatus(RideGroupStatus.OPEN);
        group.setTotalSeats(0);
        group.setTotalLuggage(0);
        group.setMaxSeats(properties.getMaxSeatsPerGroup());
        group.setMaxLuggage(properties.getMaxLuggagePerGroup());
        Instant start = request.getDesiredPickupTime().minusSeconds(properties.getTimeWindowMinutes() * 60L);
        Instant end = request.getDesiredPickupTime().plusSeconds(properties.getTimeWindowMinutes() * 60L);
        group.setTimeWindowStart(start);
        group.setTimeWindowEnd(end);
        group.setRouteDistanceKm(0.0);
        return rideGroupRepository.save(group);
    }

    @Transactional
    public void applyPlan(RideGroup group, RoutePlanner.RoutePlan plan) {
        Map<Long, RideGroupMember> existing = new HashMap<>();
        for (RideGroupMember member : memberRepository.findByRideGroupIdOrderByStopOrder(group.getId())) {
            existing.put(member.getRideRequest().getId(), member);
        }
        for (RoutePlanner.MemberPlan memberPlan : plan.getMembers()) {
            RideGroupMember member = existing.get(memberPlan.getRequest().getId());
            if (member == null) {
                member = new RideGroupMember();
                member.setRideGroup(group);
                member.setRideRequest(memberPlan.getRequest());
            }
            member.setStopOrder(memberPlan.getStopOrder());
            member.setDirectDistanceKm(memberPlan.getDirectDistanceKm());
            member.setSharedDistanceKm(memberPlan.getSharedDistanceKm());
            member.setDetourPct(memberPlan.getDetourPct());
            memberRepository.save(member);
        }
        group.setRouteDistanceKm(plan.getTotalRouteDistanceKm());
        rideGroupRepository.save(group);
    }

    @Transactional
    public void updateTotals(RideGroup group, int seatsDelta, int luggageDelta) {
        group.setTotalSeats(group.getTotalSeats() + seatsDelta);
        group.setTotalLuggage(group.getTotalLuggage() + luggageDelta);
        rideGroupRepository.save(group);
    }

    public List<RideGroupMember> getMembers(Long groupId) {
        return memberRepository.findByRideGroupIdOrderByStopOrder(groupId);
    }

    @Transactional
    public void removeMemberByRequest(Long requestId) {
        memberRepository.deleteByRideRequestId(requestId);
    }

    public RideGroup getGroup(Long id) {
        return rideGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));
    }

    public List<RideGroup> listGroups() {
        return rideGroupRepository.findAll();
    }
}
