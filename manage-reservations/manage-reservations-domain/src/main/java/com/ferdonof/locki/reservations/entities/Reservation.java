package com.ferdonof.locki.reservations.entities;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.ferdonof.locki.locations.entity.Location;
import com.ferdonof.locki.reservations.enums.ReservationStatus;

@Builder(toBuilder = true)
public record Reservation(UUID id, UUID lockerId, UUID rackId, int position, Location location, String currency, BigDecimal price,
                          ReservationStatus status, Instant startDate, Instant endDate,
                          UUID userId, Long version, Instant createdAt, Instant updatedAt) {
}
