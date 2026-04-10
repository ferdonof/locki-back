package com.ferdonof.locki.reservations.usecases;

import com.ferdonof.locki.reservations.entities.CreateReservationRequest;
import com.ferdonof.locki.reservations.entities.Reservation;

public interface CreateReservation {
  Reservation execute(CreateReservationRequest reservation);
}
