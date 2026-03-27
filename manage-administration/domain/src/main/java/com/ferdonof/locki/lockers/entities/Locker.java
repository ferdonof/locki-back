package com.ferdonof.locki.lockers.entities;

import com.ferdonof.locki.lockers.enums.LatchStatus;
import com.ferdonof.locki.lockers.enums.LockerStatus;
import com.ferdonof.locki.racks.entity.Rack;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder(toBuilder = true)
public record Locker(UUID id, int number, Rack rack, LockerStatus status, LatchStatus latchStatus, Long version,
                     Instant createdAt, Instant updatedAt) {
}
