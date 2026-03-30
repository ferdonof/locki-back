package com.ferdonof.locki.mappers;

import com.ferdonof.locki.external.admin.dto.CreateRackRequestDTO;
import com.ferdonof.locki.external.admin.dto.RackResponseDTO;
import com.ferdonof.locki.racks.entity.CreateRackRequest;
import com.ferdonof.locki.racks.entity.Rack;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RackDtoMapper {

  CreateRackRequest toDomain(CreateRackRequestDTO rackDTO);

  RackResponseDTO toDto(Rack rack);

}
