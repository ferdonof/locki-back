package com.ferdonof.locki.fee.exceptions;

import java.io.Serial;

import com.ferdonof.locki.lockers.enums.LockerSize;

public class FeeNotFoundException extends RuntimeException {
  @Serial private static final long serialVersionUID = -2380641980982268527L;

  public FeeNotFoundException(LockerSize lockerSize, String country) {
    super("Fee with locker size '%s' and country '%s' not found".formatted(lockerSize, country));
  }
}
