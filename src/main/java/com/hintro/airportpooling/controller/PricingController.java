package com.hintro.airportpooling.controller;

import com.hintro.airportpooling.dto.PriceEstimateResponse;
import com.hintro.airportpooling.entity.RideDirection;
import com.hintro.airportpooling.service.PricingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {
    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @GetMapping("/estimate")
    public PriceEstimateResponse estimate(@RequestParam RideDirection direction,
                                          @RequestParam double pickupLat,
                                          @RequestParam double pickupLng,
                                          @RequestParam double dropoffLat,
                                          @RequestParam double dropoffLng,
                                          @RequestParam(defaultValue = "1") int groupSize) {
        double directDistance = pricingService.directDistance(direction, pickupLat, pickupLng, dropoffLat, dropoffLng);
        double fare = pricingService.estimateFare(direction, pickupLat, pickupLng, dropoffLat, dropoffLng, groupSize);
        return new PriceEstimateResponse(fare, directDistance);
    }
}
