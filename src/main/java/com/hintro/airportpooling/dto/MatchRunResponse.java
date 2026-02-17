package com.hintro.airportpooling.dto;

public class MatchRunResponse {
    private int processed;
    private int matched;
    private int createdGroups;

    public MatchRunResponse(int processed, int matched, int createdGroups) {
        this.processed = processed;
        this.matched = matched;
        this.createdGroups = createdGroups;
    }

    public int getProcessed() {
        return processed;
    }

    public int getMatched() {
        return matched;
    }

    public int getCreatedGroups() {
        return createdGroups;
    }
}
