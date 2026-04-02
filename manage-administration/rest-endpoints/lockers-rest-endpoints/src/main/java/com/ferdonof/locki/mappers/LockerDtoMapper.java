package com.ferdonof.locki.mappers;

import com.ferdonof.locki.external.admin.dto.CreateLockerRequestDTO;
import com.ferdonof.locki.external.admin.dto.LockerResponseDTO;
import com.ferdonof.locki.external.admin.dto.LockersFilterRequestDTO;
import com.ferdonof.locki.external.admin.dto.UpdateLockerRequestDTO;
import com.ferdonof.locki.lockers.entities.CreateLockerRequest;
import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.entities.LockersFilter;
import com.ferdonof.locki.lockers.entities.UpdateLockerRequest;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import java.util.UUID;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface LockerDtoMapper {

  CreateLockerRequest toDomain(CreateLockerRequestDTO request);

  LockerResponseDTO toDto(Locker response);

  LockersFilter toFilter(LockersFilterRequestDTO request);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "rackId", source = "request.rackId")
  @Mapping(target = "status", source = "request.status")
  @Mapping(target = "latchStatus", source = "request.latchStatus")
  @Mapping(target = "userId", source = "request.userId")
  UpdateLockerRequest toUpdateRequest(UUID id, UpdateLockerRequestDTO request);
}
