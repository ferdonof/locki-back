package com.ferdonof.locki.users.usecases;

import com.ferdonof.locki.users.entities.LockiUser;

import java.util.UUID;

public interface GetUser {
	LockiUser execute(UUID id);
}

