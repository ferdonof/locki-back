package com.ferdonof.locki.racks.usecases;

import com.ferdonof.locki.racks.entity.Rack;
import com.ferdonof.locki.racks.entity.UpdateRackRequest;

public interface UpdateRack {
	Rack execute(UpdateRackRequest request);
}

