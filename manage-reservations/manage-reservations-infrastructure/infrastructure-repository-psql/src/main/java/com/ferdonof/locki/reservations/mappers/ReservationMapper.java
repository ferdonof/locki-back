package com.ferdonof.locki.reservations.mappers;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import com.ferdonof.locki.reservations.entities.Reservation;
import com.ferdonof.locki.reservations.entities.ReservationEntity;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ReservationMapper {

  Reservation toDomain(ReservationEntity entity);

  ReservationEntity toEntity(Reservation reservation);
}
