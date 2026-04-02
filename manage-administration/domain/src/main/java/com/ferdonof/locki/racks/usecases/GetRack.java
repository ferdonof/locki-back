package com.ferdonof.locki.racks.usecases;

import com.ferdonof.locki.racks.entity.Rack;

import java.util.UUID;

public interface GetRack {
	Rack execute(UUID id);
}

