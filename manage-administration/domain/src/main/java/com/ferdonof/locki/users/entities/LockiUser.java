package com.ferdonof.locki.users.entities;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder(toBuilder = true)
public record LockiUser(UUID id, String name, String phone, String email, Long version, Instant createdAt,
		Instant updatedAt) {
}
