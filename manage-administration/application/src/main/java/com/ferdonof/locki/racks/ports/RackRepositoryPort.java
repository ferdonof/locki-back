package com.ferdonof.locki.racks.ports;

import com.ferdonof.locki.racks.entity.Rack;

import java.util.Optional;
import java.util.UUID;

public interface RackRepositoryPort {
  Optional<Rack> findById(UUID id);
}
