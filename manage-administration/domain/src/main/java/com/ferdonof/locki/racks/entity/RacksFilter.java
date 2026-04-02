package com.ferdonof.locki.racks.entity;

import com.ferdonof.locki.racks.enums.RackStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record RacksFilter(UUID id, RackStatus status, int limit, int offset) {
}

