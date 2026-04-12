package com.ferdonof.locki.reservations.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ferdonof.locki.reservations.entities.RackedLockerEntity;

public interface RackedLockersRepository extends JpaRepository<RackedLockerEntity, UUID> {
  List<RackedLockerEntity> findByRackId(UUID id);

  Optional<RackedLockerEntity> findByLockerId(UUID id);

  @Query("""
      SELECT rl
      FROM RackedLockerEntity rl
      WHERE rl.rackId = :rackId
        AND NOT EXISTS (
          SELECT 1
          FROM ReservationEntity r
          WHERE r.lockerId = rl.lockerId
            AND r.status = 'ACTIVE'
            AND r.startDate < :endDate
            AND r.endDate > :startDate
        )
      AND rl.rackStatus = 'ACTIVE'
      AND rl.lockerStatus = 'AVAILABLE'
      ORDER BY rl.position
      LIMIT 1
      """)
  Optional<RackedLockerEntity> findOneAvailableByRackIdAndTimeSlot(
      @Param("rackId") UUID rackId,
      @Param("startDate") Instant startDate,
      @Param("endDate") Instant endDate
  );
}
