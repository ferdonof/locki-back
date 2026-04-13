package com.ferdonof.locki.locations.entity;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder(toBuilder = true)
public record Location(UUID id, UUID locationId, String address, String city, String country, String zipCode, Long version,
                       Instant createdAt, Instant updatedAt) {
}
