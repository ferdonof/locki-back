package com.ferdonof.locki.lockers.usecases;

import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.entities.UpdateLockerRequest;

public interface UpdateLocker {
	Locker execute(UpdateLockerRequest request);
}

