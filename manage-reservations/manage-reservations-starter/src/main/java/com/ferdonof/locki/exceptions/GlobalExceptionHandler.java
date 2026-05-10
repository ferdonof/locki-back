package com.ferdonof.locki.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ferdonof.locki.external.reservations.dto.ErrorDTO;
import com.ferdonof.locki.fee.exceptions.FeeNotFoundException;
import com.ferdonof.locki.lockers.exceptions.RackedLockerNotFoundException;
import com.ferdonof.locki.reservations.exceptions.LockerUnavailableException;

@RestControllerAdvice
public class GlobalExceptionHandler {


  @ExceptionHandler(LockerUnavailableException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorDTO lockerUnavailableException(LockerUnavailableException ex) {
    return new ErrorDTO()
        .code(HttpStatus.CONFLICT.value())
        .title("Conflict")
        .detail(ex.getMessage());
  }

  @ExceptionHandler(FeeNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorDTO feeNotFoundException(FeeNotFoundException ex) {
    return new ErrorDTO()
        .code(HttpStatus.NOT_FOUND.value())
        .title("Fee not found")
        .detail(ex.getMessage());
  }

  @ExceptionHandler(RackedLockerNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorDTO rackedLockerNotFound(RackedLockerNotFoundException ex) {
    return new ErrorDTO()
        .code(HttpStatus.NOT_FOUND.value())
        .title("Locker not found")
        .detail(ex.getMessage());
  }
}

