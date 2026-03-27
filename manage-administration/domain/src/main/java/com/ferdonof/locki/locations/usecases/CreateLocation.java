package com.ferdonof.locki.locations.usecases;

import com.ferdonof.locki.locations.entities.Location;

public interface CreateLocation {
  Location execute(Location location);
}
