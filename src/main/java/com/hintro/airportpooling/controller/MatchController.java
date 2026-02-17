package com.hintro.airportpooling.controller;

import com.hintro.airportpooling.dto.MatchRunResponse;
import com.hintro.airportpooling.service.MatchingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/match")
public class MatchController {
    private final MatchingService matchingService;

    public MatchController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @PostMapping("/run")
    public MatchRunResponse runMatching() {
        return matchingService.runMatchingBatch();
    }
}
