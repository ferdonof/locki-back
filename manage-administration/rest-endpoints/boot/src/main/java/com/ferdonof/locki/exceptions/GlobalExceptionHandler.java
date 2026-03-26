package com.ferdonof.locki.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ferdonof.locki.commons.exceptions.GenericClientException;
import com.ferdonof.locki.external.admin.dto.ErrorDTO;
import com.ferdonof.locki.users.exceptions.UserAlreadyExistsException;

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

	@ExceptionHandler(GenericClientException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorDTO handleUserAlreadyExists(GenericClientException ex) {
		return new ErrorDTO()
				.code(HttpStatus.BAD_REQUEST.value())
				.title("Error")
				.detail(ex.getMessage());
	}
}

