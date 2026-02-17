package com.hintro.airportpooling.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "ride_groups")
public class RideGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideGroupStatus status = RideGroupStatus.OPEN;

    @Column(name = "total_seats", nullable = false)
    private int totalSeats;

    @Column(name = "total_luggage", nullable = false)
    private int totalLuggage;

    @Column(name = "max_seats", nullable = false)
    private int maxSeats;

    @Column(name = "max_luggage", nullable = false)
    private int maxLuggage;

    @Column(name = "time_window_start", nullable = false)
    private Instant timeWindowStart;

    @Column(name = "time_window_end", nullable = false)
    private Instant timeWindowEnd;

    @Column(name = "route_distance_km", nullable = false)
    private double routeDistanceKm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cab_id")
    private Cab cab;

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

    public RideDirection getDirection() {
        return direction;
    }

    public void setDirection(RideDirection direction) {
        this.direction = direction;
    }

    public RideGroupStatus getStatus() {
        return status;
    }

    public void setStatus(RideGroupStatus status) {
        this.status = status;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getTotalLuggage() {
        return totalLuggage;
    }

    public void setTotalLuggage(int totalLuggage) {
        this.totalLuggage = totalLuggage;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public void setMaxSeats(int maxSeats) {
        this.maxSeats = maxSeats;
    }

    public int getMaxLuggage() {
        return maxLuggage;
    }

    public void setMaxLuggage(int maxLuggage) {
        this.maxLuggage = maxLuggage;
    }

    public Instant getTimeWindowStart() {
        return timeWindowStart;
    }

    public void setTimeWindowStart(Instant timeWindowStart) {
        this.timeWindowStart = timeWindowStart;
    }

    public Instant getTimeWindowEnd() {
        return timeWindowEnd;
    }

    public void setTimeWindowEnd(Instant timeWindowEnd) {
        this.timeWindowEnd = timeWindowEnd;
    }

    public double getRouteDistanceKm() {
        return routeDistanceKm;
    }

    public void setRouteDistanceKm(double routeDistanceKm) {
        this.routeDistanceKm = routeDistanceKm;
    }

    public Cab getCab() {
        return cab;
    }

    public void setCab(Cab cab) {
        this.cab = cab;
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
