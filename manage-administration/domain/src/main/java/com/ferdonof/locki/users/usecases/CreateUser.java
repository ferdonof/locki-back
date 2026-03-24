package com.ferdonof.locki.users.usecases;

import com.ferdonof.locki.users.entities.LockiUser;

public interface CreateUser {
	LockiUser execute(LockiUser lockiUser);
}
