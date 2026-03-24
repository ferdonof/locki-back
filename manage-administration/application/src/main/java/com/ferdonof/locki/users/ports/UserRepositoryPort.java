package com.ferdonof.locki.users.ports;

import com.ferdonof.locki.users.entities.LockiUser;

public interface UserRepositoryPort {
	LockiUser insert(LockiUser lockiUser);
}
