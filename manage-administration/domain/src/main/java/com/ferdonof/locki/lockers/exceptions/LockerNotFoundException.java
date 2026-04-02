package com.ferdonof.locki.lockers.exceptions;

import java.io.Serial;
import java.util.UUID;

public class LockerNotFoundException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	public LockerNotFoundException(UUID id) {
		super("Locker with id '%s' not found".formatted(id));
	}
}

