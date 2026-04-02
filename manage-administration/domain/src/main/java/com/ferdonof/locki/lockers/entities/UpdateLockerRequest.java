package com.ferdonof.locki.lockers.entities;

import com.ferdonof.locki.lockers.enums.LatchStatus;
import com.ferdonof.locki.lockers.enums.LockerStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UpdateLockerRequest(UUID id, UUID rackId, LockerStatus status, LatchStatus latchStatus, UUID userId) {
}

