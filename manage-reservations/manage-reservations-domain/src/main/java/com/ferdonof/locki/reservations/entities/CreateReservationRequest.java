package com.ferdonof.locki.reservations.entities;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder(toBuilder = true)
public record CreateReservationRequest(UUID locationId, UUID lockerId, UUID userId, Instant startDate, Instant endDate) {
}
