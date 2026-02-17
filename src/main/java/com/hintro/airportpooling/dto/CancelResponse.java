package com.hintro.airportpooling.dto;

public class CancelResponse {
    private Long requestId;
    private String status;

    public CancelResponse(Long requestId, String status) {
        this.requestId = requestId;
        this.status = status;
    }

    public Long getRequestId() {
        return requestId;
    }

    public String getStatus() {
        return status;
    }
}
