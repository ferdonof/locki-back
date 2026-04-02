package com.ferdonof.locki.lockers.ports;

import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.entities.LockersFilter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LockerRepositoryPort {

  Locker insert(Locker locker);

  Optional<Locker> findById(UUID id);

  Locker update(Locker locker);

  List<Locker> search(LockersFilter filter);

  List<Locker> findByRackId(UUID rackId);
}
