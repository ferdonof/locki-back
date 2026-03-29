package com.ferdonof.locki.racks.usecases;

import com.ferdonof.locki.racks.entity.CreateRackRequest;
import com.ferdonof.locki.racks.entity.Rack;

public interface CreateRack {
  Rack execute(CreateRackRequest rack);
}
