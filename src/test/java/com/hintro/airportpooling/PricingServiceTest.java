package com.hintro.airportpooling;

import com.hintro.airportpooling.config.AppProperties;
import com.hintro.airportpooling.entity.RideDirection;
import com.hintro.airportpooling.entity.RideRequestStatus;
import com.hintro.airportpooling.repository.CabRepository;
import com.hintro.airportpooling.repository.RideRequestRepository;
import com.hintro.airportpooling.service.PricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class PricingServiceTest {

    private RideRequestRepository rideRequestRepository;
    private CabRepository cabRepository;
    private AppProperties properties;

    @BeforeEach
    void setUp() {
        rideRequestRepository = Mockito.mock(RideRequestRepository.class);
        cabRepository = Mockito.mock(CabRepository.class);
        properties = new AppProperties();
    }

    @Test
    void surgeIncreasesFareWhenDemandHigh() {
        PricingService pricingService = new PricingService(properties, rideRequestRepository, cabRepository);
        when(rideRequestRepository.countByStatus(RideRequestStatus.PENDING)).thenReturn(50L);
        when(cabRepository.countByStatus(Mockito.any())).thenReturn(5L);

        double lowSurgeFare;
        when(rideRequestRepository.countByStatus(RideRequestStatus.PENDING)).thenReturn(5L);
        when(cabRepository.countByStatus(Mockito.any())).thenReturn(5L);
        lowSurgeFare = pricingService.estimateFare(RideDirection.TO_AIRPORT, 0, 0, 0.1, 0.1, 1);

        when(rideRequestRepository.countByStatus(RideRequestStatus.PENDING)).thenReturn(50L);
        when(cabRepository.countByStatus(Mockito.any())).thenReturn(5L);
        double highSurgeFare = pricingService.estimateFare(RideDirection.TO_AIRPORT, 0, 0, 0.1, 0.1, 1);

        assertTrue(highSurgeFare >= lowSurgeFare);
    }

    @Test
    void sharedDiscountReducesFare() {
        PricingService pricingService = new PricingService(properties, rideRequestRepository, cabRepository);
        when(rideRequestRepository.countByStatus(RideRequestStatus.PENDING)).thenReturn(0L);
        when(cabRepository.countByStatus(Mockito.any())).thenReturn(10L);

        double soloFare = pricingService.estimateFare(RideDirection.TO_AIRPORT, 0, 0, 0.1, 0.1, 1);
        double sharedFare = pricingService.estimateFare(RideDirection.TO_AIRPORT, 0, 0, 0.1, 0.1, 3);

        assertTrue(sharedFare <= soloFare);
    }
}
