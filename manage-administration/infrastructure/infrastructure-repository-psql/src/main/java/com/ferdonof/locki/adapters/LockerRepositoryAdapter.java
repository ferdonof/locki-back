package com.ferdonof.locki.adapters;

import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.ports.LockerRepositoryPort;
import com.ferdonof.locki.mappers.LockerMapper;
import com.ferdonof.locki.repositories.LockerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LockerRepositoryAdapter implements LockerRepositoryPort {

  private final LockerMapper lockerMapper;

  private final LockerRepository lockerRepository;

  @Override
  public Locker insert(Locker locker) {
    final var lockerEntity = this.lockerMapper.toEntity(locker);
    return this.lockerMapper.toDomain(this.lockerRepository.save(lockerEntity));
  }
}
