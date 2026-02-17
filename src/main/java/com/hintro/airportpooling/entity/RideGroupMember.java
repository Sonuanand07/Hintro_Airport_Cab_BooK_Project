package com.hintro.airportpooling.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "ride_group_members")
public class RideGroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_group_id", nullable = false)
    private RideGroup rideGroup;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_request_id", nullable = false, unique = true)
    private RideRequest rideRequest;

    @Column(name = "stop_order", nullable = false)
    private int stopOrder;

    @Column(name = "direct_distance_km", nullable = false)
    private double directDistanceKm;

    @Column(name = "shared_distance_km", nullable = false)
    private double sharedDistanceKm;

    @Column(name = "detour_pct", nullable = false)
    private double detourPct;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RideGroup getRideGroup() {
        return rideGroup;
    }

    public void setRideGroup(RideGroup rideGroup) {
        this.rideGroup = rideGroup;
    }

    public RideRequest getRideRequest() {
        return rideRequest;
    }

    public void setRideRequest(RideRequest rideRequest) {
        this.rideRequest = rideRequest;
    }

    public int getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(int stopOrder) {
        this.stopOrder = stopOrder;
    }

    public double getDirectDistanceKm() {
        return directDistanceKm;
    }

    public void setDirectDistanceKm(double directDistanceKm) {
        this.directDistanceKm = directDistanceKm;
    }

    public double getSharedDistanceKm() {
        return sharedDistanceKm;
    }

    public void setSharedDistanceKm(double sharedDistanceKm) {
        this.sharedDistanceKm = sharedDistanceKm;
    }

    public double getDetourPct() {
        return detourPct;
    }

    public void setDetourPct(double detourPct) {
        this.detourPct = detourPct;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
