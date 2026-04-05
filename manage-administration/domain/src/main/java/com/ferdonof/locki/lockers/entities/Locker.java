package com.ferdonof.locki.lockers.entities;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

import com.ferdonof.locki.lockers.enums.LatchStatus;
import com.ferdonof.locki.lockers.enums.LockerSize;
import com.ferdonof.locki.lockers.enums.LockerStatus;
import com.ferdonof.locki.racks.entity.Rack;

@Builder(toBuilder = true)
public record Locker(UUID id, int serial, int position, Rack rack, LockerStatus status, LatchStatus latchStatus,
                     LockerSize size, Long version, Instant createdAt, Instant updatedAt) {
}
