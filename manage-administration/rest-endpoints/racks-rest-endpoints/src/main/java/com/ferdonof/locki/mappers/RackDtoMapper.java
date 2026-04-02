package com.ferdonof.locki.mappers;

import com.ferdonof.locki.external.admin.dto.CreateRackRequestDTO;
import com.ferdonof.locki.external.admin.dto.LockerResponseDTO;
import com.ferdonof.locki.external.admin.dto.RackResponseDTO;
import com.ferdonof.locki.external.admin.dto.RacksFilterRequestDTO;
import com.ferdonof.locki.external.admin.dto.UpdateRackRequestDTO;
import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.racks.entity.CreateRackRequest;
import com.ferdonof.locki.racks.entity.Rack;
import com.ferdonof.locki.racks.entity.RacksFilter;
import com.ferdonof.locki.racks.entity.UpdateRackRequest;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import java.util.UUID;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RackDtoMapper {

  CreateRackRequest toDomain(CreateRackRequestDTO rackDTO);

  RackResponseDTO toDto(Rack rack);

  LockerResponseDTO toLockerDto(Locker locker);

  @Mapping(target = "status", ignore = true) // API spec bug: uses LockerStatusDTO instead of RackStatusDTO
  RacksFilter toFilter(RacksFilterRequestDTO request);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "status", source = "request.status")
  @Mapping(target = "location", source = "request.location")
  UpdateRackRequest toUpdateRequest(UUID id, UpdateRackRequestDTO request);
}
