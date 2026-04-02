package com.ferdonof.locki.mappers;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

import com.ferdonof.locki.external.admin.dto.CreateUserRequestDTO;
import com.ferdonof.locki.external.admin.dto.UserFilterRequestDTO;
import com.ferdonof.locki.external.admin.dto.UserResponseDTO;
import com.ferdonof.locki.users.entities.LockiUser;
import com.ferdonof.locki.users.entities.UserFilter;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserDtoMapper {

	LockiUser toDomain(CreateUserRequestDTO request);

	UserResponseDTO toDto(LockiUser response);

	UserFilter toFilter(UserFilterRequestDTO request);
}
