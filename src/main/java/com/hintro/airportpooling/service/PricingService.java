package com.hintro.airportpooling.service;

import com.hintro.airportpooling.config.AppProperties;
import com.hintro.airportpooling.entity.CabStatus;
import com.hintro.airportpooling.entity.RideDirection;
import com.hintro.airportpooling.entity.RideRequestStatus;
import com.hintro.airportpooling.repository.CabRepository;
import com.hintro.airportpooling.repository.RideRequestRepository;
import com.hintro.airportpooling.util.GeoUtils;
import org.springframework.stereotype.Service;

@Service
public class PricingService {
    private final AppProperties properties;
    private final RideRequestRepository rideRequestRepository;
    private final CabRepository cabRepository;

    public PricingService(AppProperties properties,
                          RideRequestRepository rideRequestRepository,
                          CabRepository cabRepository) {
        this.properties = properties;
        this.rideRequestRepository = rideRequestRepository;
        this.cabRepository = cabRepository;
    }

    public double estimateFare(RideDirection direction, double pickupLat, double pickupLng, double dropoffLat, double dropoffLng, int groupSize) {
        double directDistance = directDistance(direction, pickupLat, pickupLng, dropoffLat, dropoffLng);
        double base = properties.getPricing().getBaseFare();
        double perKm = properties.getPricing().getPerKm();
        double sharedDiscount = Math.min(properties.getPricing().getMaxSharedDiscount(),
                Math.max(0, groupSize - 1) * properties.getPricing().getSharedDiscountPerExtra());
        double surge = computeSurgeMultiplier();
        double fare = (base + perKm * directDistance) * surge * (1.0 - sharedDiscount);
        return Math.max(fare, base);
    }

    public double directDistance(RideDirection direction, double pickupLat, double pickupLng, double dropoffLat, double dropoffLng) {
        if (direction == RideDirection.FROM_AIRPORT) {
            return GeoUtils.distanceKm(properties.getAirport().getLat(), properties.getAirport().getLng(), dropoffLat, dropoffLng);
        }
        return GeoUtils.distanceKm(pickupLat, pickupLng, properties.getAirport().getLat(), properties.getAirport().getLng());
    }

    private double computeSurgeMultiplier() {
        long pending = rideRequestRepository.countByStatus(RideRequestStatus.PENDING);
        long availableCabs = cabRepository.countByStatus(CabStatus.AVAILABLE);
        double ratio = pending / (double) Math.max(1, availableCabs);
        double surge = properties.getPricing().getSurgeBase() + ratio * 0.05;
        return Math.max(1.0, Math.min(surge, 2.0));
    }
}
