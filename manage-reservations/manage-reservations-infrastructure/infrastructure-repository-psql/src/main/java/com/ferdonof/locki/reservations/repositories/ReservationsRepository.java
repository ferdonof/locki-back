package com.ferdonof.locki.reservations.repositories;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ferdonof.locki.reservations.entities.ReservationEntity;
import com.ferdonof.locki.reservations.enums.ReservationStatus;

public interface ReservationsRepository extends JpaRepository<ReservationEntity, UUID> {

  Optional<ReservationEntity> findByLockerIdAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndStatusIs(UUID lockerId,
      Instant startDate, Instant endDate, ReservationStatus status);
}
