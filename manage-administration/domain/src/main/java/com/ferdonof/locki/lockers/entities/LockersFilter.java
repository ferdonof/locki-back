package com.ferdonof.locki.lockers.entities;

import com.ferdonof.locki.lockers.enums.LatchStatus;
import com.ferdonof.locki.lockers.enums.LockerStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record LockersFilter(UUID id, Integer number, UUID rackId, UUID userId, LockerStatus status,
		LatchStatus latchStatus, int limit, int offset) {
}
