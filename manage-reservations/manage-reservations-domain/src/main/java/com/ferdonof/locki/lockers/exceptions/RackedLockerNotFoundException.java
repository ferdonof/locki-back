package com.ferdonof.locki.lockers.exceptions;

import java.io.Serial;
import java.util.UUID;

public class RackedLockerNotFoundException extends RuntimeException {
  @Serial private static final long serialVersionUID = 934008311766269749L;

  public RackedLockerNotFoundException(UUID id) {
    super("Locker with id '%s' not found".formatted(id));
  }
}
