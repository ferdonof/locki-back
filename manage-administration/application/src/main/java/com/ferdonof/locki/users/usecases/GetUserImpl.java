package com.ferdonof.locki.users.usecases;

import com.ferdonof.locki.users.entities.LockiUser;
import com.ferdonof.locki.users.exceptions.UserNotFoundException;
import com.ferdonof.locki.users.ports.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class GetUserImpl implements GetUser {

	private final UserRepositoryPort userRepository;

	@Override
	public LockiUser execute(UUID id) {
		log.info("Getting user with id {}", id);
		return this.userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException(id));
	}
}

