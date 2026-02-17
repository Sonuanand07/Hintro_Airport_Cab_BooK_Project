package com.hintro.airportpooling.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "ride_requests")
public class RideRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideDirection direction;

    @Column(name = "pickup_lat", nullable = false)
    private double pickupLat;

    @Column(name = "pickup_lng", nullable = false)
    private double pickupLng;

    @Column(name = "dropoff_lat", nullable = false)
    private double dropoffLat;

    @Column(name = "dropoff_lng", nullable = false)
    private double dropoffLng;

    @Column(name = "seats_required", nullable = false)
    private int seatsRequired;

    @Column(name = "luggage_units", nullable = false)
    private int luggageUnits;

    @Column(name = "max_detour_pct", nullable = false)
    private double maxDetourPct;

    @Column(name = "desired_pickup_time", nullable = false)
    private Instant desiredPickupTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideRequestStatus status = RideRequestStatus.PENDING;

    @Column(name = "fare_estimate", nullable = false)
    private double fareEstimate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_group_id")
    private RideGroup assignedGroup;

    @Version
    @Column(nullable = false)
    private int version;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public RideDirection getDirection() {
        return direction;
    }

    public void setDirection(RideDirection direction) {
        this.direction = direction;
    }

    public double getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(double pickupLat) {
        this.pickupLat = pickupLat;
    }

    public double getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(double pickupLng) {
        this.pickupLng = pickupLng;
    }

    public double getDropoffLat() {
        return dropoffLat;
    }

    public void setDropoffLat(double dropoffLat) {
        this.dropoffLat = dropoffLat;
    }

    public double getDropoffLng() {
        return dropoffLng;
    }

    public void setDropoffLng(double dropoffLng) {
        this.dropoffLng = dropoffLng;
    }

    public int getSeatsRequired() {
        return seatsRequired;
    }

    public void setSeatsRequired(int seatsRequired) {
        this.seatsRequired = seatsRequired;
    }

    public int getLuggageUnits() {
        return luggageUnits;
    }

    public void setLuggageUnits(int luggageUnits) {
        this.luggageUnits = luggageUnits;
    }

    public double getMaxDetourPct() {
        return maxDetourPct;
    }

    public void setMaxDetourPct(double maxDetourPct) {
        this.maxDetourPct = maxDetourPct;
    }

    public Instant getDesiredPickupTime() {
        return desiredPickupTime;
    }

    public void setDesiredPickupTime(Instant desiredPickupTime) {
        this.desiredPickupTime = desiredPickupTime;
    }

    public RideRequestStatus getStatus() {
        return status;
    }

    public void setStatus(RideRequestStatus status) {
        this.status = status;
    }

    public double getFareEstimate() {
        return fareEstimate;
    }

    public void setFareEstimate(double fareEstimate) {
        this.fareEstimate = fareEstimate;
    }

    public RideGroup getAssignedGroup() {
        return assignedGroup;
    }

    public void setAssignedGroup(RideGroup assignedGroup) {
        this.assignedGroup = assignedGroup;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
