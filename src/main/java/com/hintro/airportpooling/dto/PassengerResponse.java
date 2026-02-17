package com.hintro.airportpooling.dto;

public class PassengerResponse {
    private Long id;
    private String name;
    private String phone;

    public PassengerResponse(Long id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
