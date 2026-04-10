package com.ferdonof.locki.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ferdonof.locki.external.reservations.dto.CreateReservationRequestDTO;
import com.ferdonof.locki.external.reservations.dto.ReservationDTO;
import com.ferdonof.locki.mappers.ReservationDtoMapper;
import com.ferdonof.locki.reservations.entities.CreateReservationRequest;
import com.ferdonof.locki.reservations.usecases.CreateReservation;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationsController {

  private final ReservationDtoMapper reservationDtoMapper;

  private final CreateReservation createReservation;

  @PostMapping
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public ReservationDTO createReservation(@RequestBody @Valid CreateReservationRequestDTO request) {
    final CreateReservationRequest reservationRequest = this.reservationDtoMapper.toDomain(request);
    return this.reservationDtoMapper.toDto(this.createReservation.execute(reservationRequest));
  }
}
