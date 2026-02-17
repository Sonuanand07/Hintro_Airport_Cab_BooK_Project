package com.hintro.airportpooling.controller;

import com.hintro.airportpooling.dto.RideGroupResponse;
import com.hintro.airportpooling.entity.RideGroup;
import com.hintro.airportpooling.service.DtoMapper;
import com.hintro.airportpooling.service.GroupCacheService;
import com.hintro.airportpooling.service.RideGroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class RideGroupController {
    private final RideGroupService rideGroupService;
    private final GroupCacheService cacheService;

    public RideGroupController(RideGroupService rideGroupService, GroupCacheService cacheService) {
        this.rideGroupService = rideGroupService;
        this.cacheService = cacheService;
    }

    @GetMapping("/{id}")
    public RideGroupResponse getGroup(@PathVariable Long id) {
        return cacheService.getGroup(id)
                .orElseGet(() -> {
                    RideGroup group = rideGroupService.getGroup(id);
                    RideGroupResponse response = DtoMapper.toRideGroupResponse(group, rideGroupService.getMembers(id));
                    cacheService.putGroup(response);
                    return response;
                });
    }

    @GetMapping
    public List<RideGroupResponse> listGroups() {
        return rideGroupService.listGroups().stream()
                .map(group -> DtoMapper.toRideGroupResponse(group, rideGroupService.getMembers(group.getId())))
                .toList();
    }
}
