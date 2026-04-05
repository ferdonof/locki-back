package com.ferdonof.locki.lockers.entities;

import lombok.Builder;

import java.util.UUID;

import com.ferdonof.locki.lockers.enums.LatchStatus;
import com.ferdonof.locki.lockers.enums.LockerStatus;

@Builder
public record LockersFilter(UUID id, Integer serial, UUID rackId, UUID userId, LockerStatus status,
                            LatchStatus latchStatus, int limit, int offset, String sort) {
}
