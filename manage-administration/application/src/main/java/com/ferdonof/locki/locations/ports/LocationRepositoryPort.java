package com.ferdonof.locki.locations.ports;

import com.ferdonof.locki.locations.entities.Location;

public interface LocationRepositoryPort {
  Location insert(Location location);
}
