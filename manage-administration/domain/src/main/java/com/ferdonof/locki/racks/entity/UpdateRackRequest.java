package com.ferdonof.locki.racks.entity;

import com.ferdonof.locki.locations.entities.Location;
import com.ferdonof.locki.racks.enums.RackStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateRackRequest(UUID id, RackStatus status, Location location) {
}

