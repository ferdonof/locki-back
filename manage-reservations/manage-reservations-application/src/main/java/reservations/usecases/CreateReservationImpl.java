package reservations.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reservations.ports.RackedLockersRepositoryPort;
import reservations.ports.ReservationsRepositoryPort;
import static com.ferdonof.locki.reservations.enums.ReservationStatus.ACTIVE;

import org.springframework.transaction.support.TransactionTemplate;

import com.ferdonof.locki.fee.entities.Fee;
import com.ferdonof.locki.fee.exceptions.FeeNotFoundException;
import com.ferdonof.locki.fee.ports.FeeCachePort;
import com.ferdonof.locki.lockers.entities.RackedLocker;
import com.ferdonof.locki.lockers.exceptions.RackedLockerNotFoundException;
import com.ferdonof.locki.reservations.entities.CreateReservationRequest;
import com.ferdonof.locki.reservations.entities.Reservation;
import com.ferdonof.locki.reservations.exceptions.LockerUnavailableException;
import com.ferdonof.locki.reservations.usecases.CreateReservation;

@Slf4j
@RequiredArgsConstructor
public class CreateReservationImpl implements CreateReservation {
  private final TransactionTemplate transactionTemplate;

  private final FeeCachePort feeCachePort;

  private final RackedLockersRepositoryPort rackedLockersRepositoryPort;

  private final ReservationsRepositoryPort reservationsRepositoryPort;

  @Override
  public Reservation execute(CreateReservationRequest request) {
    log.info("Creating reservation for locker with id '{}'", request.lockerId());
    return this.transactionTemplate.execute(status -> {

      final RackedLocker rackedLocker = request.lockerId() != null
          ? this.withSpecificLocker(request)
          : this.withRackOnly(request);

      final Fee fee = this.feeCachePort
          .get(Fee
              .builder()
              .lockerSize(rackedLocker.size())
              .country(rackedLocker
                  .location()
                  .country())
              .build())
          .orElseThrow(() -> new FeeNotFoundException(rackedLocker.size(), rackedLocker
              .location()
              .country()));

      final Reservation reservation = this.buildReservation(request, rackedLocker, fee);

      return this.reservationsRepositoryPort.insert(reservation);
    });
  }

  private Reservation buildReservation(CreateReservationRequest request, RackedLocker rackedLocker, Fee fee) {
    return Reservation
        .builder()
        .lockerId(rackedLocker.lockerId())
        .rackId(rackedLocker.rackId())
        .position(rackedLocker.position())
        .price(fee.price())
        .currency(fee.currency())
        .status(ACTIVE)
        .startDate(request.startDate())
        .endDate(request.endDate())
        .userId(request.userId())
        .location(rackedLocker.location())
        .build();
  }

  private RackedLocker withSpecificLocker(CreateReservationRequest request) {
    this.reservationsRepositoryPort
        .findByLockerUnavailableInTimeSlot(request.lockerId(), request.startDate(), request.endDate())
        .ifPresent(reservation -> {
          throw new LockerUnavailableException(request.lockerId());
        });

    return this.rackedLockersRepositoryPort
        .findByLockerId(request.lockerId())
        .orElseThrow(() -> new RackedLockerNotFoundException(request.lockerId()));
  }

  private RackedLocker withRackOnly(CreateReservationRequest request) {
    return this.rackedLockersRepositoryPort
        .findAnyAvailableByRackId(request.rackId(), request.startDate(), request.endDate())
        .orElseThrow(() -> new RackedLockerNotFoundException(request.lockerId()));
  }
}
