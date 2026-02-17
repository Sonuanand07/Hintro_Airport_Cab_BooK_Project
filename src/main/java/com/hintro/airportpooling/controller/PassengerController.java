package com.hintro.airportpooling.controller;

import com.hintro.airportpooling.dto.CreatePassengerRequest;
import com.hintro.airportpooling.dto.PassengerResponse;
import com.hintro.airportpooling.service.DtoMapper;
import com.hintro.airportpooling.service.PassengerService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {
    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @PostMapping
    public PassengerResponse createPassenger(@Valid @RequestBody CreatePassengerRequest request) {
        return DtoMapper.toPassengerResponse(passengerService.createPassenger(request.getName(), request.getPhone()));
    }

    @GetMapping("/{id}")
    public PassengerResponse getPassenger(@PathVariable Long id) {
        return DtoMapper.toPassengerResponse(passengerService.getPassenger(id));
    }
}
