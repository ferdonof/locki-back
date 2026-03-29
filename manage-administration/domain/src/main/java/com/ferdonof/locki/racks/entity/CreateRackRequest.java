package com.ferdonof.locki.racks.entity;

import com.ferdonof.locki.racks.enums.RackStatus;
import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record CreateRackRequest(int number, RackStatus status, UUID locationId) {
}
