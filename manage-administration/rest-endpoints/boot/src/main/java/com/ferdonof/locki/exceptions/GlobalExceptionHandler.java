package com.ferdonof.locki.exceptions;

import com.ferdonof.locki.external.admin.dto.ErrorDTO;
import com.ferdonof.locki.users.exceptions.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UserAlreadyExistsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErrorDTO handleUserAlreadyExists(UserAlreadyExistsException ex) {
		return new ErrorDTO()
				.code(HttpStatus.CONFLICT.value())
				.title("Conflict")
				.detail(ex.getMessage());
	}
}

