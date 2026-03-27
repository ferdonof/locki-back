package com.ferdonof.locki.adapters;

import com.ferdonof.locki.entities.UserEntity;
import com.ferdonof.locki.mappers.LockiUserMapper;
import com.ferdonof.locki.repositories.UserRepository;
import com.ferdonof.locki.users.entities.LockiUser;
import com.ferdonof.locki.users.exceptions.UserAlreadyExistsException;
import com.ferdonof.locki.users.ports.UserRepositoryPort;
import com.ferdonof.locki.utils.ConstraintViolationHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.ferdonof.locki.enums.ConstraintValidationsConstants.UK_USERS_EMAIL;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

	private final UserRepository userRepository;
	private final LockiUserMapper lockiUserMapper;

	@Override
	public LockiUser insert(LockiUser lockiUser) {
		final UserEntity userEntity = this.lockiUserMapper.toEntity(lockiUser);

		final UserEntity savedEntity = ConstraintViolationHandler.executeOrThrow(
				() -> this.userRepository.saveAndFlush(userEntity),
				Map.of(UK_USERS_EMAIL, () -> new UserAlreadyExistsException(lockiUser.email())));

		return this.lockiUserMapper.toDomain(savedEntity);
	}
}
