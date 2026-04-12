package com.ferdonof.locki.lockers.entities;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

import com.ferdonof.locki.locations.entity.Location;
import com.ferdonof.locki.lockers.enums.LockerSize;
import com.ferdonof.locki.lockers.enums.LockerStatus;
import com.ferdonof.locki.lockers.enums.RackStatus;

@Builder(toBuilder = true)
public record RackedLocker(UUID id, UUID lockerId, UUID rackId, int position, RackStatus rackStatus, Location location,
                           LockerStatus lockerStatus, LockerSize size, Long version, Instant createdAt, Instant updatedAt) {
}
