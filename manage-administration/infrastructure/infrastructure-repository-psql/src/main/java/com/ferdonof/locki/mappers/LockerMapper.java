package com.ferdonof.locki.mappers;

import com.ferdonof.locki.entities.LockerEntity;
import com.ferdonof.locki.lockers.entities.Locker;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface LockerMapper {

  LockerEntity toEntity(Locker locker);

  Locker toDomain(LockerEntity lockerEntity);
}
