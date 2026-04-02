package com.ferdonof.locki.lockers.usecases;

import com.ferdonof.locki.lockers.entities.Locker;

import java.util.UUID;

public interface GetLocker {
	Locker execute(UUID id);
}

