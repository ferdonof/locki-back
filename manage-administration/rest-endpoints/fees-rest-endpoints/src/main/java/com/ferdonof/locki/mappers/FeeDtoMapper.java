package com.ferdonof.locki.mappers;

import java.util.UUID;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import com.ferdonof.locki.external.admin.dto.CreateFeeRequestDTO;
import com.ferdonof.locki.external.admin.dto.FeeResponseDTO;
import com.ferdonof.locki.external.admin.dto.SizeDTO;
import com.ferdonof.locki.fees.entities.Fee;
import com.ferdonof.locki.lockers.enums.LockerSize;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    componentModel = "spring",
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface FeeDtoMapper {

  @Mapping(source = "lockerSize", target = "lockerSize", qualifiedByName = "sizeDtoToLockerSize")
  Fee toDomain(CreateFeeRequestDTO dto);

  @Mapping(source = "lockerSize", target = "lockerSize", qualifiedByName = "lockerSizeToSizeDto")
  FeeResponseDTO toDto(Fee fee);

  @Mapping(source = "dto.lockerSize", target = "lockerSize", qualifiedByName = "sizeDtoToLockerSize")
  Fee toUpdateRequest(UUID id, CreateFeeRequestDTO dto);

  @Named("sizeDtoToLockerSize")
  default LockerSize sizeDtoToLockerSize(SizeDTO sizeDto) {
    if (sizeDto == null) {
      return null;
    }
    return LockerSize.valueOf(sizeDto.getValue());
  }

  @Named("lockerSizeToSizeDto")
  default SizeDTO lockerSizeToSizeDto(LockerSize lockerSize) {
    if (lockerSize == null) {
      return null;
    }
    return SizeDTO.fromValue(lockerSize.name());
  }
}

