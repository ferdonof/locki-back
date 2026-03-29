package com.ferdonof.locki.locations.ports;

import com.ferdonof.locki.locations.entities.Location;

import java.util.Optional;
import java.util.UUID;

public interface LocationRepositoryPort {
  Location insert(Location location);

  Optional<Location> findById(UUID uuid);
}
