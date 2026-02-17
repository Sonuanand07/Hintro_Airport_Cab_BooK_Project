package com.hintro.airportpooling.dto;

public class PriceEstimateResponse {
    private double fareEstimate;
    private double directDistanceKm;

    public PriceEstimateResponse(double fareEstimate, double directDistanceKm) {
        this.fareEstimate = fareEstimate;
        this.directDistanceKm = directDistanceKm;
    }

    public double getFareEstimate() {
        return fareEstimate;
    }

    public double getDirectDistanceKm() {
        return directDistanceKm;
    }
}
