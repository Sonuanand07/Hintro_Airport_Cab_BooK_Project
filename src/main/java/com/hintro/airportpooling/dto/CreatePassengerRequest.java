package com.hintro.airportpooling.dto;

import jakarta.validation.constraints.NotBlank;

public class CreatePassengerRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String phone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
