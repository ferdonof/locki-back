package com.ferdonof.locki.fee.exceptions;

import java.io.Serial;
import java.util.UUID;

public class FeeNotFoundException extends RuntimeException {
  @Serial private static final long serialVersionUID = -2380641980982268527L;

  public FeeNotFoundException(UUID id) {
    super("Fee with id %s not found".formatted(id));
  }
}
