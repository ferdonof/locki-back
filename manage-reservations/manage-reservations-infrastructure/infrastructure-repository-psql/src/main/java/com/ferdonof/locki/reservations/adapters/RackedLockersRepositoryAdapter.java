package com.ferdonof.locki.reservations.adapters;

import lombok.RequiredArgsConstructor;
import reservations.ports.RackedLockersRepositoryPort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ferdonof.locki.lockers.entities.RackedLocker;
import com.ferdonof.locki.reservations.mappers.RackedLockerMapper;
import com.ferdonof.locki.reservations.repositories.RackedLockersRepository;

@Component
@RequiredArgsConstructor
public class RackedLockersRepositoryAdapter implements RackedLockersRepositoryPort {

  private final RackedLockerMapper rackedLockerMapper;

  private final RackedLockersRepository rackedLockersRepository;

  @Override
  public RackedLocker insert(RackedLocker rackedLocker) {
    return this.rackedLockerMapper.toDomain(
        this.rackedLockersRepository.saveAndFlush(
            this.rackedLockerMapper.toEntity(rackedLocker)
        )
    );
  }

  @Override
  public Optional<RackedLocker> findById(UUID id) {
    return this.rackedLockersRepository
        .findById(id)
        .map(this.rackedLockerMapper::toDomain);
  }

  @Override
  public RackedLocker update(RackedLocker rackedLocker) {
    return this.rackedLockerMapper.toDomain(
        this.rackedLockersRepository.saveAndFlush(
            this.rackedLockerMapper.toEntity(rackedLocker)
        )
    );
  }

  @Override
  public List<RackedLocker> findByRackerId(UUID id) {
    return this.rackedLockersRepository
        .findByRackId(id)
        .stream()
        .map(this.rackedLockerMapper::toDomain)
        .toList();
  }

  @Override
  public Optional<RackedLocker> findByLockerId(UUID id) {
    return this.rackedLockersRepository
        .findByLockerId(id)
        .map(this.rackedLockerMapper::toDomain);
  }
}
