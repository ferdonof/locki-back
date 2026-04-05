package com.ferdonof.locki.lockers.entities;

import lombok.Builder;

import java.util.UUID;

import com.ferdonof.locki.lockers.enums.LatchStatus;
import com.ferdonof.locki.lockers.enums.LockerStatus;

@Builder
public record CreateLockerRequest(int serial, UUID rackId, LockerStatus status, LatchStatus latchStatus) {
}
