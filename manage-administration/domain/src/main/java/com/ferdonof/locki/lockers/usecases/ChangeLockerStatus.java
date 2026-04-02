package com.ferdonof.locki.lockers.usecases;

import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.enums.LockerStatus;

import java.util.UUID;

public interface ChangeLockerStatus {
	Locker execute(UUID id, LockerStatus status);
}

