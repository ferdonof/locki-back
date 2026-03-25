package com.ferdonof.locki.users.exceptions;

import java.io.Serial;

public class UserAlreadyExistsException extends RuntimeException {

  @Serial private static final long serialVersionUID = -7324470436553837264L;

  public UserAlreadyExistsException(String email) {
		super("User with email '%s' already exists".formatted(email));
	}
}

