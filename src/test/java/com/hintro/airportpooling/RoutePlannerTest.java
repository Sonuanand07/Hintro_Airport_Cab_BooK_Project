package com.hintro.airportpooling;

import com.hintro.airportpooling.entity.RideDirection;
import com.hintro.airportpooling.entity.RideGroupMember;
import com.hintro.airportpooling.entity.RideRequest;
import com.hintro.airportpooling.service.RoutePlanner;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoutePlannerTest {

    @Test
    void computeBestInsertionRespectsDetourLimits() {
        RoutePlanner planner = new RoutePlanner();
        double hubLat = 0.0;
        double hubLng = 0.0;

        RideRequest existing = new RideRequest();
        existing.setDirection(RideDirection.FROM_AIRPORT);
        existing.setDropoffLat(0.0);
        existing.setDropoffLng(1.0);
        existing.setMaxDetourPct(50.0);
        existing.setSeatsRequired(1);
        existing.setLuggageUnits(0);
        existing.setDesiredPickupTime(Instant.now());

        RideGroupMember member = new RideGroupMember();
        member.setRideRequest(existing);
        member.setStopOrder(0);

        RideRequest incoming = new RideRequest();
        incoming.setDirection(RideDirection.FROM_AIRPORT);
        incoming.setDropoffLat(0.0);
        incoming.setDropoffLng(2.0);
        incoming.setMaxDetourPct(50.0);
        incoming.setSeatsRequired(1);
        incoming.setLuggageUnits(0);
        incoming.setDesiredPickupTime(Instant.now());

        RoutePlanner.RoutePlan plan = planner.computeBestInsertion(
                RideDirection.FROM_AIRPORT,
                hubLat,
                hubLng,
                List.of(member),
                incoming
        );

        assertNotNull(plan, "Plan should be feasible with generous detour limits");
        assertEquals(2, plan.getMembers().size());
        assertTrue(plan.getMembers().stream().allMatch(p -> p.getDetourPct() <= 50.0));
    }

    @Test
    void computeBestInsertionRejectsWhenDetourTooHigh() {
        RoutePlanner planner = new RoutePlanner();
        double hubLat = 0.0;
        double hubLng = 0.0;

        RideRequest existing = new RideRequest();
        existing.setDirection(RideDirection.FROM_AIRPORT);
        existing.setDropoffLat(0.0);
        existing.setDropoffLng(1.0);
        existing.setMaxDetourPct(5.0);
        existing.setSeatsRequired(1);
        existing.setLuggageUnits(0);
        existing.setDesiredPickupTime(Instant.now());

        RideGroupMember member = new RideGroupMember();
        member.setRideRequest(existing);
        member.setStopOrder(0);

        RideRequest incoming = new RideRequest();
        incoming.setDirection(RideDirection.FROM_AIRPORT);
        incoming.setDropoffLat(0.0);
        incoming.setDropoffLng(3.0);
        incoming.setMaxDetourPct(5.0);
        incoming.setSeatsRequired(1);
        incoming.setLuggageUnits(0);
        incoming.setDesiredPickupTime(Instant.now());

        RoutePlanner.RoutePlan plan = planner.computeBestInsertion(
                RideDirection.FROM_AIRPORT,
                hubLat,
                hubLng,
                List.of(member),
                incoming
        );

        assertNull(plan, "Plan should be rejected due to tight detour limits");
    }
}
