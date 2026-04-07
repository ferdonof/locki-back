package com.ferdonof.locki.mappers;

import com.ferdonof.locki.entities.FeeEntity;
import com.ferdonof.locki.fees.entities.Fee;
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
public interface FeeMapper {

  FeeEntity toEntity(Fee fee);

  Fee toDomain(FeeEntity entity);
}

