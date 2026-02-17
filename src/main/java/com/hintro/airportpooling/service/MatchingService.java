package com.hintro.airportpooling.service;

import com.hintro.airportpooling.config.AppProperties;
import com.hintro.airportpooling.dto.MatchRunResponse;
import com.hintro.airportpooling.entity.RideGroup;
import com.hintro.airportpooling.entity.RideGroupMember;
import com.hintro.airportpooling.entity.RideGroupStatus;
import com.hintro.airportpooling.entity.RideRequest;
import com.hintro.airportpooling.entity.RideRequestStatus;
import com.hintro.airportpooling.repository.RideGroupMemberRepository;
import com.hintro.airportpooling.repository.RideGroupRepository;
import com.hintro.airportpooling.repository.RideRequestRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class MatchingService {
    private final RideRequestRepository rideRequestRepository;
    private final RideGroupRepository rideGroupRepository;
    private final RideGroupMemberRepository memberRepository;
    private final RideGroupService rideGroupService;
    private final CabService cabService;
    private final AppProperties properties;
    private final RoutePlanner routePlanner = new RoutePlanner();

    public MatchingService(RideRequestRepository rideRequestRepository,
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

    @Scheduled(fixedDelayString = "${pooling.matching.interval-ms}")
    public void scheduledMatch() {
        runMatchingBatch();
    }

    @Transactional
    public MatchRunResponse runMatchingBatch() {
        Instant fromTime = Instant.now().minusSeconds(properties.getTimeWindowMinutes() * 60L);
        int batchSize = properties.getMatching().getBatchSize();
        List<RideRequest> pending = rideRequestRepository.findPendingForUpdate(
                RideRequestStatus.PENDING,
                fromTime,
                PageRequest.of(0, batchSize)
        );
        int processed = 0;
        int matched = 0;
        int createdGroups = 0;
        for (RideRequest request : pending) {
            if (request.getStatus() != RideRequestStatus.PENDING) {
                continue;
            }
            processed++;
            MatchResult result = matchSingleRequest(request);
            matched += result.matched ? 1 : 0;
            createdGroups += result.createdGroup ? 1 : 0;
        }
        return new MatchRunResponse(processed, matched, createdGroups);
    }

    private MatchResult matchSingleRequest(RideRequest request) {
        List<RideGroup> openGroups = rideGroupRepository.findOpenGroups(
                RideGroupStatus.OPEN, request.getDirection(), request.getDesiredPickupTime());

        RideGroup bestGroup = null;
        RoutePlanner.RoutePlan bestPlan = null;
        for (RideGroup group : openGroups) {
            if (!hasCapacity(group, request)) {
                continue;
            }
            List<RideGroupMember> members = memberRepository.findByRideGroupIdOrderByStopOrder(group.getId());
            RoutePlanner.RoutePlan plan = routePlanner.computeBestInsertion(
                    group.getDirection(),
                    properties.getAirport().getLat(),
                    properties.getAirport().getLng(),
                    members,
                    request);
            if (plan == null) {
                continue;
            }
            if (bestPlan == null || plan.getTotalRouteDistanceKm() < bestPlan.getTotalRouteDistanceKm()) {
                bestPlan = plan;
                bestGroup = group;
            }
        }

        if (bestGroup == null) {
            RideGroup newGroup = rideGroupService.createGroup(request);
            RoutePlanner.RoutePlan plan = routePlanner.computeBestInsertion(
                    newGroup.getDirection(),
                    properties.getAirport().getLat(),
                    properties.getAirport().getLng(),
                    List.of(),
                    request);
            if (plan == null) {
                return new MatchResult(false, false);
            }
            applyPlanAndAssign(newGroup, plan);
            return new MatchResult(true, true);
        }

        RideGroup lockedGroup = rideGroupRepository.findForUpdate(bestGroup.getId());
        if (!hasCapacity(lockedGroup, request)) {
            return new MatchResult(false, false);
        }
        List<RideGroupMember> lockedMembers = memberRepository.findByRideGroupIdOrderByStopOrder(lockedGroup.getId());
        RoutePlanner.RoutePlan plan = routePlanner.computeBestInsertion(
                lockedGroup.getDirection(),
                properties.getAirport().getLat(),
                properties.getAirport().getLng(),
                lockedMembers,
                request);
        if (plan == null) {
            return new MatchResult(false, false);
        }
        applyPlanAndAssign(lockedGroup, plan);
        return new MatchResult(true, false);
    }

    private boolean hasCapacity(RideGroup group, RideRequest request) {
        return group.getTotalSeats() + request.getSeatsRequired() <= group.getMaxSeats()
                && group.getTotalLuggage() + request.getLuggageUnits() <= group.getMaxLuggage();
    }

    private void applyPlanAndAssign(RideGroup group, RoutePlanner.RoutePlan plan) {
        int totalSeats = plan.getMembers().stream().mapToInt(p -> p.getRequest().getSeatsRequired()).sum();
        int totalLuggage = plan.getMembers().stream().mapToInt(p -> p.getRequest().getLuggageUnits()).sum();
        group.setTotalSeats(totalSeats);
        group.setTotalLuggage(totalLuggage);
        rideGroupRepository.save(group);
        rideGroupService.applyPlan(group, plan);

        for (RoutePlanner.MemberPlan memberPlan : plan.getMembers()) {
            RideRequest request = memberPlan.getRequest();
            request.setAssignedGroup(group);
            request.setStatus(RideRequestStatus.ASSIGNED);
            rideRequestRepository.save(request);
        }

        if (group.getTotalSeats() >= group.getMaxSeats()) {
            assignCabIfAvailable(group);
        }
    }

    private void assignCabIfAvailable(RideGroup group) {
        if (group.getCab() != null) {
            return;
        }
        cabService.allocateCab(group.getTotalSeats(), group.getTotalLuggage())
                .ifPresent(cab -> {
                    group.setCab(cab);
                    group.setStatus(RideGroupStatus.READY);
                    rideGroupRepository.save(group);
                });
    }

    private static class MatchResult {
        private final boolean matched;
        private final boolean createdGroup;

        private MatchResult(boolean matched, boolean createdGroup) {
            this.matched = matched;
            this.createdGroup = createdGroup;
        }
    }
}
