package com.ferdonof.locki.users.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.ferdonof.locki.users.entities.LockiUser;
import com.ferdonof.locki.users.ports.UserRepositoryPort;

@Slf4j
@RequiredArgsConstructor
public class CreateUserImpl implements CreateUser {

	private final UserRepositoryPort userRepository;

	@Override
	public LockiUser execute(LockiUser lockiUser) {
		log.info("Creating user with email {}", lockiUser.email());
		return this.userRepository.insert(lockiUser);

	}
}
