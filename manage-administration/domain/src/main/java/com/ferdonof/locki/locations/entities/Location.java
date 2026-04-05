package com.ferdonof.locki.locations.entities;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record Location(UUID id, String name, String address, String city, String country, String code,
                       BigDecimal lat, BigDecimal lon, Long version, Instant createdAt, Instant updatedAt) {
}
