package com.ferdonof.locki.users.entities;

import java.time.Instant;
import java.util.UUID;

public record LockiUser(UUID id, String name, String phone, String email, Long version, Instant createdAt,
		Instant updatedAt) {
}
