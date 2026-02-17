package com.hintro.airportpooling.service;

import com.hintro.airportpooling.config.AppProperties;
import com.hintro.airportpooling.dto.CreateRideRequest;
import com.hintro.airportpooling.entity.RideRequest;
import com.hintro.airportpooling.entity.RideRequestStatus;
import com.hintro.airportpooling.repository.RideRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RideRequestService {
    private final RideRequestRepository rideRequestRepository;
    private final PassengerService passengerService;
    private final PricingService pricingService;
    private final AppProperties properties;

    public RideRequestService(RideRequestRepository rideRequestRepository,
                              PassengerService passengerService,
                              PricingService pricingService,
                              AppProperties properties) {
        this.rideRequestRepository = rideRequestRepository;
        this.passengerService = passengerService;
        this.pricingService = pricingService;
        this.properties = properties;
    }

    @Transactional
    public RideRequest createRequest(CreateRideRequest request) {
        if (request.getSeatsRequired() > properties.getMaxSeatsPerGroup()) {
            throw new IllegalArgumentException("Seats required exceed max seats per group");
        }
        if (request.getLuggageUnits() > properties.getMaxLuggagePerGroup()) {
            throw new IllegalArgumentException("Luggage units exceed max luggage per group");
        }
        RideRequest rideRequest = new RideRequest();
        rideRequest.setPassenger(passengerService.getPassenger(request.getPassengerId()));
        rideRequest.setDirection(request.getDirection());
        rideRequest.setPickupLat(request.getPickupLat());
        rideRequest.setPickupLng(request.getPickupLng());
        rideRequest.setDropoffLat(request.getDropoffLat());
        rideRequest.setDropoffLng(request.getDropoffLng());
        rideRequest.setSeatsRequired(request.getSeatsRequired());
        rideRequest.setLuggageUnits(request.getLuggageUnits());
        double maxDetour = request.getMaxDetourPct() == null ? properties.getDetour().getDefaultMaxPct() : request.getMaxDetourPct();
        rideRequest.setMaxDetourPct(maxDetour);
        rideRequest.setDesiredPickupTime(request.getDesiredPickupTime());
        rideRequest.setStatus(RideRequestStatus.PENDING);
        double fareEstimate = pricingService.estimateFare(
                request.getDirection(),
                request.getPickupLat(),
                request.getPickupLng(),
                request.getDropoffLat(),
                request.getDropoffLng(),
                1
        );
        rideRequest.setFareEstimate(fareEstimate);
        return rideRequestRepository.save(rideRequest);
    }

    public RideRequest getRequest(Long id) {
        return rideRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
    }
}
