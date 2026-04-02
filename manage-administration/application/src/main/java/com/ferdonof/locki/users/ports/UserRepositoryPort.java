package com.ferdonof.locki.users.ports;

import com.ferdonof.locki.users.entities.LockiUser;
import com.ferdonof.locki.users.entities.UserFilter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
	LockiUser insert(LockiUser lockiUser);

	Optional<LockiUser> findById(UUID id);

	List<LockiUser> search(UserFilter filter);
}
