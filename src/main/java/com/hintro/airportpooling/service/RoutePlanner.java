package com.hintro.airportpooling.service;

import com.hintro.airportpooling.entity.RideDirection;
import com.hintro.airportpooling.entity.RideGroupMember;
import com.hintro.airportpooling.entity.RideRequest;
import com.hintro.airportpooling.util.GeoUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RoutePlanner {

    public RoutePlan computeBestInsertion(RideDirection direction,
                                          double hubLat,
                                          double hubLng,
                                          List<RideGroupMember> existingMembers,
                                          RideRequest newRequest) {
        List<RideRequest> ordered = existingMembers.stream()
                .sorted(Comparator.comparingInt(RideGroupMember::getStopOrder))
                .map(RideGroupMember::getRideRequest)
                .toList();

        RoutePlan best = null;
        for (int i = 0; i <= ordered.size(); i++) {
            List<RideRequest> candidate = new ArrayList<>(ordered.size() + 1);
            candidate.addAll(ordered.subList(0, i));
            candidate.add(newRequest);
            candidate.addAll(ordered.subList(i, ordered.size()));

            RoutePlan plan = buildPlan(direction, hubLat, hubLng, candidate);
            if (plan == null) {
                continue;
            }
            if (best == null || plan.getTotalRouteDistanceKm() < best.getTotalRouteDistanceKm()) {
                best = plan;
            }
        }
        return best;
    }

    public RoutePlan rebuildPlan(RideDirection direction,
                                 double hubLat,
                                 double hubLng,
                                 List<RideGroupMember> members) {
        List<RideRequest> ordered = members.stream()
                .sorted(Comparator.comparingInt(RideGroupMember::getStopOrder))
                .map(RideGroupMember::getRideRequest)
                .toList();
        return buildPlan(direction, hubLat, hubLng, ordered);
    }

    private RoutePlan buildPlan(RideDirection direction,
                                double hubLat,
                                double hubLng,
                                List<RideRequest> orderedRequests) {
        if (orderedRequests.isEmpty()) {
            return null;
        }
        double[] segmentDistances = computeSegmentDistances(direction, hubLat, hubLng, orderedRequests);
        double totalRouteDistance = 0.0;
        for (double d : segmentDistances) {
            totalRouteDistance += d;
        }

        List<MemberPlan> members = new ArrayList<>();
        if (direction == RideDirection.FROM_AIRPORT) {
            double prefix = 0.0;
            for (int i = 0; i < orderedRequests.size(); i++) {
                prefix += segmentDistances[i];
                RideRequest request = orderedRequests.get(i);
                double direct = directDistance(direction, hubLat, hubLng, request);
                double detourPct = detourPct(prefix, direct);
                if (detourPct > request.getMaxDetourPct()) {
                    return null;
                }
                members.add(new MemberPlan(request, i, direct, prefix, detourPct));
            }
        } else {
            double[] suffix = new double[orderedRequests.size()];
            double running = 0.0;
            for (int i = orderedRequests.size() - 1; i >= 0; i--) {
                running += segmentDistances[i];
                suffix[i] = running;
            }
            for (int i = 0; i < orderedRequests.size(); i++) {
                RideRequest request = orderedRequests.get(i);
                double direct = directDistance(direction, hubLat, hubLng, request);
                double detourPct = detourPct(suffix[i], direct);
                if (detourPct > request.getMaxDetourPct()) {
                    return null;
                }
                members.add(new MemberPlan(request, i, direct, suffix[i], detourPct));
            }
        }
        return new RoutePlan(members, totalRouteDistance);
    }

    private double[] computeSegmentDistances(RideDirection direction,
                                             double hubLat,
                                             double hubLng,
                                             List<RideRequest> orderedRequests) {
        double[] segments = new double[orderedRequests.size()];
        if (direction == RideDirection.FROM_AIRPORT) {
            double prevLat = hubLat;
            double prevLng = hubLng;
            for (int i = 0; i < orderedRequests.size(); i++) {
                RideRequest req = orderedRequests.get(i);
                segments[i] = GeoUtils.distanceKm(prevLat, prevLng, req.getDropoffLat(), req.getDropoffLng());
                prevLat = req.getDropoffLat();
                prevLng = req.getDropoffLng();
            }
        } else {
            for (int i = 0; i < orderedRequests.size(); i++) {
                RideRequest req = orderedRequests.get(i);
                double nextLat;
                double nextLng;
                if (i == orderedRequests.size() - 1) {
                    nextLat = hubLat;
                    nextLng = hubLng;
                } else {
                    RideRequest next = orderedRequests.get(i + 1);
                    nextLat = next.getPickupLat();
                    nextLng = next.getPickupLng();
                }
                segments[i] = GeoUtils.distanceKm(req.getPickupLat(), req.getPickupLng(), nextLat, nextLng);
            }
        }
        return segments;
    }

    private double directDistance(RideDirection direction, double hubLat, double hubLng, RideRequest request) {
        if (direction == RideDirection.FROM_AIRPORT) {
            return GeoUtils.distanceKm(hubLat, hubLng, request.getDropoffLat(), request.getDropoffLng());
        }
        return GeoUtils.distanceKm(request.getPickupLat(), request.getPickupLng(), hubLat, hubLng);
    }

    private double detourPct(double sharedDistance, double directDistance) {
        if (directDistance <= 0) {
            return 0.0;
        }
        return ((sharedDistance - directDistance) / directDistance) * 100.0;
    }

    public static class RoutePlan {
        private final List<MemberPlan> members;
        private final double totalRouteDistanceKm;

        public RoutePlan(List<MemberPlan> members, double totalRouteDistanceKm) {
            this.members = members;
            this.totalRouteDistanceKm = totalRouteDistanceKm;
        }

        public List<MemberPlan> getMembers() {
            return members;
        }

        public double getTotalRouteDistanceKm() {
            return totalRouteDistanceKm;
        }
    }

    public static class MemberPlan {
        private final RideRequest request;
        private final int stopOrder;
        private final double directDistanceKm;
        private final double sharedDistanceKm;
        private final double detourPct;

        public MemberPlan(RideRequest request, int stopOrder, double directDistanceKm, double sharedDistanceKm, double detourPct) {
            this.request = request;
            this.stopOrder = stopOrder;
            this.directDistanceKm = directDistanceKm;
            this.sharedDistanceKm = sharedDistanceKm;
            this.detourPct = detourPct;
        }

        public RideRequest getRequest() {
            return request;
        }

        public int getStopOrder() {
            return stopOrder;
        }

        public double getDirectDistanceKm() {
            return directDistanceKm;
        }

        public double getSharedDistanceKm() {
            return sharedDistanceKm;
        }

        public double getDetourPct() {
            return detourPct;
        }
    }
}
