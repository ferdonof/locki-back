package com.ferdonof.locki.reservations.adapters;

import lombok.RequiredArgsConstructor;
import reservations.ports.ReservationsRepositoryPort;
import static com.ferdonof.locki.reservations.enums.ReservationStatus.ACTIVE;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ferdonof.locki.reservations.entities.Reservation;
import com.ferdonof.locki.reservations.entities.ReservationEntity;
import com.ferdonof.locki.reservations.mappers.ReservationMapper;
import com.ferdonof.locki.reservations.repositories.ReservationsRepository;

@Component
@RequiredArgsConstructor
public class ReservationsRepositoryAdapter implements ReservationsRepositoryPort {

  private final ReservationMapper reservationMapper;

  private final ReservationsRepository reservationsRepository;

  @Override
  public Reservation insert(Reservation reservation) {
    final ReservationEntity entity = this.reservationMapper.toEntity(reservation);
    final ReservationEntity reservationEntity = this.reservationsRepository
        .saveAndFlush(entity);
    return this.reservationMapper.toDomain(reservationEntity);
  }

  @Override
  public Optional<Reservation> findById(UUID id) {
    return this.reservationsRepository
        .findById(id)
        .map(this.reservationMapper::toDomain);
  }

  @Override
  public Reservation update(Reservation reservation) {
    final ReservationEntity entity = this.reservationMapper.toEntity(reservation);
    final ReservationEntity reservationEntity = this.reservationsRepository
        .saveAndFlush(entity);
    return this.reservationMapper.toDomain(reservationEntity);
  }

  @Override
  public Optional<Reservation> findByLockerUnavailableInTimeSlot(UUID lockerId, Instant from, Instant to) {
    return this.reservationsRepository
        .findByLockerIdAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndStatusIs(lockerId, from, to, ACTIVE)
        .map(this.reservationMapper::toDomain);
  }
}
