package com.ferdonof.locki.adapters;

import com.ferdonof.locki.entities.UserEntity;
import com.ferdonof.locki.mappers.LockiUserMapper;
import com.ferdonof.locki.repositories.UserRepository;
import com.ferdonof.locki.users.entities.LockiUser;
import com.ferdonof.locki.users.entities.UserFilter;
import com.ferdonof.locki.users.exceptions.UserAlreadyExistsException;
import com.ferdonof.locki.users.ports.UserRepositoryPort;
import com.ferdonof.locki.utils.ConstraintViolationHandler;
import com.ferdonof.locki.utils.PaginationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

	@Override
	public Optional<LockiUser> findById(UUID id) {
		return this.userRepository.findById(id)
				.map(this.lockiUserMapper::toDomain);
	}

	@Override
	public List<LockiUser> search(UserFilter filter) {
		final var pageable = PaginationValidator.toPageable(filter.offset(), filter.limit());
		return this.userRepository.findAll(pageable)
				.getContent()
				.stream()
				.map(this.lockiUserMapper::toDomain)
				.toList();
	}
}
