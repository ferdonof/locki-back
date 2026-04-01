package com.ferdonof.locki.users.exceptions;

import java.io.Serial;
import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	public UserNotFoundException(UUID id) {
		super("User with id '%s' not found".formatted(id));
	}
}

