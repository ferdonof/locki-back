package com.ferdonof.locki.adapters;

import com.ferdonof.locki.entities.UserEntity;
import com.ferdonof.locki.mappers.UserMapper;
import com.ferdonof.locki.repositories.UserRepository;
import com.ferdonof.locki.users.entities.LockiUser;
import com.ferdonof.locki.users.ports.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

	private final UserRepository userRepository;
	private final UserMapper userMapper;

	@Override
	public LockiUser insert(LockiUser lockiUser) {
		final UserEntity userEntity = this.userMapper.toEntity(lockiUser);
		final UserEntity savedEntity = this.userRepository.save(userEntity);
		return this.userMapper.toDomain(savedEntity);
	}
}
