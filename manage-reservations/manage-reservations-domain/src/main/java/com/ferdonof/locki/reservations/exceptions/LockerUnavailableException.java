package com.ferdonof.locki.reservations.exceptions;

import java.io.Serial;
import java.util.UUID;

public class LockerUnavailableException extends RuntimeException {
  @Serial private static final long serialVersionUID = 8095113722190176991L;

  public LockerUnavailableException(UUID lockerId) {
    super("Locker with id '%s' is unavailable in the selected time slot".formatted(lockerId));
  }
}
