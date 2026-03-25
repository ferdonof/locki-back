package com.ferdonof.locki.adapters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static com.ferdonof.locki.enums.ConstraintValidationsConstants.UK_USERS_EMAIL;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ferdonof.locki.entities.UserEntity;
import com.ferdonof.locki.mappers.UserMapper;
import com.ferdonof.locki.repositories.UserRepository;
import com.ferdonof.locki.users.entities.LockiUser;
import com.ferdonof.locki.users.exceptions.UserAlreadyExistsException;
import com.ferdonof.locki.users.ports.UserRepositoryPort;
import com.ferdonof.locki.utils.ConstraintViolationHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

	private final UserRepository userRepository;
	private final UserMapper userMapper;

	@Override
	public LockiUser insert(LockiUser lockiUser) {
		final UserEntity userEntity = this.userMapper.toEntity(lockiUser);

		final UserEntity savedEntity = ConstraintViolationHandler.executeOrThrow(
				() -> this.userRepository.saveAndFlush(userEntity),
				List.of(UK_USERS_EMAIL),
				() -> new UserAlreadyExistsException(lockiUser.email()));

		return this.userMapper.toDomain(savedEntity);
	}
}
