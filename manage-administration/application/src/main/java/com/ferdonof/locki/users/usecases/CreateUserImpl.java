package com.ferdonof.locki.users.usecases;

import com.ferdonof.locki.users.entities.LockiUser;
import com.ferdonof.locki.users.ports.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@RequiredArgsConstructor
public class CreateUserImpl implements CreateUser {
	private final TransactionTemplate transactionTemplate;

	private final UserRepositoryPort userRepository;

	@Override
	public LockiUser execute(LockiUser lockiUser) {
		log.info("Creating user with email {}", lockiUser.email());
		return this.transactionTemplate.execute(status -> this.userRepository.insert(lockiUser));
	}
}
