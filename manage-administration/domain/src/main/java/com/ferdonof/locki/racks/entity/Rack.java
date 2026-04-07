package com.ferdonof.locki.racks.entity;

import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.ferdonof.locki.locations.entities.Location;
import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.racks.enums.RackStatus;

@Builder(toBuilder = true)
public record Rack(UUID id, int serial, int size, RackStatus status, Location location, List<Locker> lockers,
                   Long version, Instant createdAt, Instant updatedAt) {
}
