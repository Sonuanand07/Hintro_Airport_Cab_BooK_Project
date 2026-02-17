package com.hintro.airportpooling.controller;

import com.hintro.airportpooling.dto.CancelResponse;
import com.hintro.airportpooling.dto.CreateRideRequest;
import com.hintro.airportpooling.dto.RideRequestResponse;
import com.hintro.airportpooling.entity.RideRequest;
import com.hintro.airportpooling.service.CancellationService;
import com.hintro.airportpooling.service.DtoMapper;
import com.hintro.airportpooling.service.RideRequestService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/requests")
public class RideRequestController {
    private final RideRequestService rideRequestService;
    private final CancellationService cancellationService;

    public RideRequestController(RideRequestService rideRequestService,
                                 CancellationService cancellationService) {
        this.rideRequestService = rideRequestService;
        this.cancellationService = cancellationService;
    }

    @PostMapping
    public RideRequestResponse createRequest(@Valid @RequestBody CreateRideRequest request) {
        RideRequest rideRequest = rideRequestService.createRequest(request);
        return DtoMapper.toRideRequestResponse(rideRequest);
    }

    @GetMapping("/{id}")
    public RideRequestResponse getRequest(@PathVariable Long id) {
        return DtoMapper.toRideRequestResponse(rideRequestService.getRequest(id));
    }

    @PostMapping("/{id}/cancel")
    public CancelResponse cancelRequest(@PathVariable Long id) {
        RideRequest request = cancellationService.cancelRequest(id);
        return new CancelResponse(request.getId(), request.getStatus().name());
    }
}
