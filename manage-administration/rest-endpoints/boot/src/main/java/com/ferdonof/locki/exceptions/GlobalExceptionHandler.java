package com.ferdonof.locki.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ferdonof.locki.commons.exceptions.GenericClientException;
import com.ferdonof.locki.external.admin.dto.ErrorDTO;
import com.ferdonof.locki.fees.exceptions.FeeNotFoundException;
import com.ferdonof.locki.lockers.exceptions.LockerNotFoundException;
import com.ferdonof.locki.racks.exceptions.RackNotFoundException;
import com.ferdonof.locki.users.exceptions.UserAlreadyExistsException;
import com.ferdonof.locki.users.exceptions.UserNotFoundException;

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

  @ExceptionHandler(UserNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorDTO handleUserNotFound(UserNotFoundException ex) {
    return new ErrorDTO()
        .code(HttpStatus.NOT_FOUND.value())
        .title("Not Found")
        .detail(ex.getMessage());
  }

  @ExceptionHandler(RackNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorDTO handleRackNotFound(RackNotFoundException ex) {
    return new ErrorDTO()
        .code(HttpStatus.NOT_FOUND.value())
        .title("Not Found")
        .detail(ex.getMessage());
  }

  @ExceptionHandler(LockerNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorDTO handleLockerNotFound(LockerNotFoundException ex) {
    return new ErrorDTO()
        .code(HttpStatus.NOT_FOUND.value())
        .title("Not Found")
        .detail(ex.getMessage());
  }

  @ExceptionHandler(GenericClientException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDTO handleGenericClientError(GenericClientException ex) {
    return new ErrorDTO()
        .code(HttpStatus.BAD_REQUEST.value())
        .title("Error")
        .detail(ex.getMessage());
  }

  @ExceptionHandler(FeeNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorDTO handleLockerNotFound(FeeNotFoundException ex) {
    return new ErrorDTO()
        .code(HttpStatus.NOT_FOUND.value())
        .title("Not Found")
        .detail(ex.getMessage());
  }
}

