package com.ferdonof.locki.lockers.usecases;

import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.entities.LockersFilter;

import java.util.List;

public interface SearchLockers {
	List<Locker> execute(LockersFilter filter);
}

