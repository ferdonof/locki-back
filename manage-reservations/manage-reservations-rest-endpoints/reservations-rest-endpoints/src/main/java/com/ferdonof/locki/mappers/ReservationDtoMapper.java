package com.ferdonof.locki.mappers;

import java.time.Instant;
import java.time.OffsetDateTime;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import com.ferdonof.locki.external.reservations.dto.CreateReservationRequestDTO;
import com.ferdonof.locki.external.reservations.dto.ReservationDTO;
import com.ferdonof.locki.reservations.entities.CreateReservationRequest;
import com.ferdonof.locki.reservations.entities.Reservation;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ReservationDtoMapper {

  CreateReservationRequest toDomain(CreateReservationRequestDTO request);

  ReservationDTO toDto(Reservation reservation);

  default Instant map(OffsetDateTime date) {
    return date == null
        ? null
        : date.toInstant();
  }

  default OffsetDateTime map(Instant instant) {
    return instant == null
        ? null
        : instant.atOffset(java.time.ZoneOffset.UTC);
  }
}
