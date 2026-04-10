package com.ferdonof.locki.reservations.mappers;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import com.ferdonof.locki.lockers.entities.RackedLocker;
import com.ferdonof.locki.reservations.entities.RackedLockerEntity;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RackedLockerMapper {

  RackedLocker toDomain(RackedLockerEntity entity);

  RackedLockerEntity toEntity(RackedLocker rackedLocker);
}
