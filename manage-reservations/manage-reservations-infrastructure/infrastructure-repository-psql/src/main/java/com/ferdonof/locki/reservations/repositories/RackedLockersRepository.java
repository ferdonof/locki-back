package com.ferdonof.locki.reservations.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ferdonof.locki.reservations.entities.RackedLockerEntity;

public interface RackedLockersRepository extends JpaRepository<RackedLockerEntity, UUID> {
  List<RackedLockerEntity> findByRackId(UUID id);

  Optional<RackedLockerEntity> findByLockerId(UUID id);
}
