package com.hintro.airportpooling.controller;

import com.hintro.airportpooling.dto.CabResponse;
import com.hintro.airportpooling.dto.CreateCabRequest;
import com.hintro.airportpooling.service.CabService;
import com.hintro.airportpooling.service.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cabs")
public class CabController {
    private final CabService cabService;

    public CabController(CabService cabService) {
        this.cabService = cabService;
    }

    @PostMapping
    public CabResponse createCab(@Valid @RequestBody CreateCabRequest request) {
        return DtoMapper.toCabResponse(cabService.createCab(request.getPlateNumber(), request.getSeatCapacity(), request.getLuggageCapacity()));
    }

    @GetMapping
    public List<CabResponse> listCabs() {
        return cabService.listCabs().stream().map(DtoMapper::toCabResponse).toList();
    }
}
