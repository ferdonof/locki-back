package com.ferdonof.locki.locations.usecases;

import com.ferdonof.locki.locations.entity.Location;

public interface UpsertLocation {
  Location execute(Location location);
}
