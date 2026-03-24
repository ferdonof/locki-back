package com.ferdonof.locki.mappers;

import com.ferdonof.locki.entities.UserEntity;
import com.ferdonof.locki.users.entities.LockiUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

	LockiUser toDomain(UserEntity userEntity);

	UserEntity toEntity(LockiUser user);
}
