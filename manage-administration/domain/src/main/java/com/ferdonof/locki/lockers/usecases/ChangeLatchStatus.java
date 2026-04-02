package com.ferdonof.locki.lockers.usecases;

import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.enums.LatchStatus;

import java.util.UUID;

public interface ChangeLatchStatus {
	Locker execute(UUID id, LatchStatus status);
}

