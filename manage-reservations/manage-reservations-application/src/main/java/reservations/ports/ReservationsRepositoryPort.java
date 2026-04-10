package reservations.ports;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.ferdonof.locki.reservations.entities.Reservation;

public interface ReservationsRepositoryPort {

  Reservation insert(Reservation reservation);

  Optional<Reservation> findById(UUID id);

  Reservation update(Reservation reservation);

  Optional<Reservation> findByLockerUnavailableInTimeSlot(UUID lockerId, Instant from, Instant to);
}
