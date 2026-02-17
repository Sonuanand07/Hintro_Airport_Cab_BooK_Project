package com.hintro.airportpooling.repository;

import com.hintro.airportpooling.entity.RideGroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RideGroupMemberRepository extends JpaRepository<RideGroupMember, Long> {
    List<RideGroupMember> findByRideGroupIdOrderByStopOrder(Long rideGroupId);
    Optional<RideGroupMember> findByRideRequestId(Long rideRequestId);
    void deleteByRideRequestId(Long rideRequestId);
}
