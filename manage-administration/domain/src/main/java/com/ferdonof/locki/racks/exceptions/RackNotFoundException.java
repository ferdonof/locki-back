package com.ferdonof.locki.racks.exceptions;

import java.io.Serial;
import java.util.UUID;

public class RackNotFoundException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	public RackNotFoundException(UUID id) {
		super("Rack with id '%s' not found".formatted(id));
	}
}

