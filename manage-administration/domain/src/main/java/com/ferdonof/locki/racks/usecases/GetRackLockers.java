package com.ferdonof.locki.racks.usecases;

import com.ferdonof.locki.lockers.entities.Locker;

import java.util.List;
import java.util.UUID;

public interface GetRackLockers {
	List<Locker> execute(UUID rackId);
}

