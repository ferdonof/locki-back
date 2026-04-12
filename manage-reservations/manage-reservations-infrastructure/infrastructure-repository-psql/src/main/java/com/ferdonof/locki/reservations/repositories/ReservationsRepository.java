package com.ferdonof.locki.reservations.repositories;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ferdonof.locki.reservations.entities.ReservationEntity;
import com.ferdonof.locki.reservations.enums.ReservationStatus;

public interface ReservationsRepository extends JpaRepository<ReservationEntity, UUID> {

  @Query("""
        SELECT r FROM ReservationEntity r
        JOIN RackedLockerEntity rl ON r.lockerId = rl.lockerId AND r.rackId = rl.rackId
        WHERE r.lockerId = :lockerId
          AND r.startDate >= :from
          AND r.endDate <= :to
          AND r.status = :status
          AND rl.lockerStatus NOT IN ('MAINTENANCE', 'OUT_OF_SERVICE')
          AND rl.rackStatus = 'ACTIVE'
      """)
  Optional<ReservationEntity> findByLockerIdAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndStatusIs(
      @Param("lockerId") UUID lockerId,
      @Param("from") Instant startDate,
      @Param("to") Instant endDate,
      @Param("status") ReservationStatus status
  );
}
