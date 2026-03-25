package com.ferdonof.locki.mappers;

import com.ferdonof.locki.entities.UserEntity;
import com.ferdonof.locki.users.entities.LockiUser;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "spring", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface LockiUserMapper {

	LockiUser toDomain(UserEntity userEntity);

	UserEntity toEntity(LockiUser user);
}
