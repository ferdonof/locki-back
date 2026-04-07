package com.ferdonof.locki.racks.entity;

import lombok.Builder;

import java.util.UUID;

import com.ferdonof.locki.racks.enums.RackStatus;

@Builder(toBuilder = true)
public record CreateRackRequest(int serial, int size, RackStatus status, UUID locationId) {
}
