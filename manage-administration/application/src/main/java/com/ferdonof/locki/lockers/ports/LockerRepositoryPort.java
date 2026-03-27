package com.ferdonof.locki.lockers.ports;

import com.ferdonof.locki.lockers.entities.Locker;

public interface LockerRepositoryPort {

  Locker insert(Locker locker);
}
