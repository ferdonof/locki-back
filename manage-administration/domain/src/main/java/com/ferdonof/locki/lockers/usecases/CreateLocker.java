package com.ferdonof.locki.lockers.usecases;

import com.ferdonof.locki.lockers.entities.CreateLockerRequest;
import com.ferdonof.locki.lockers.entities.Locker;

public interface CreateLocker {
  Locker execute(CreateLockerRequest request);
}
