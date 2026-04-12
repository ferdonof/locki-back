package reservations.ports;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ferdonof.locki.lockers.entities.RackedLocker;

public interface RackedLockersRepositoryPort {

  RackedLocker insert(RackedLocker rackedLocker);

  Optional<RackedLocker> findById(UUID id);

  RackedLocker update(RackedLocker rackedLocker);

  List<RackedLocker> findByRackerId(UUID id);

  Optional<RackedLocker> findByLockerId(UUID uuid);

  Optional<RackedLocker> findAnyAvailableByRackId(UUID rackId, Instant startDate, Instant endDate);
}
