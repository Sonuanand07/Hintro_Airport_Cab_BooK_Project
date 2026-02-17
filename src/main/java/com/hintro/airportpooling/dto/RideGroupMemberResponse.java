package com.hintro.airportpooling.dto;

public class RideGroupMemberResponse {
    private Long requestId;
    private int stopOrder;
    private double detourPct;

    public RideGroupMemberResponse(Long requestId, int stopOrder, double detourPct) {
        this.requestId = requestId;
        this.stopOrder = stopOrder;
        this.detourPct = detourPct;
    }

    public Long getRequestId() {
        return requestId;
    }

    public int getStopOrder() {
        return stopOrder;
    }

    public double getDetourPct() {
        return detourPct;
    }
}
