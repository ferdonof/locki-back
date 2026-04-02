package com.ferdonof.locki.racks.ports;

import com.ferdonof.locki.racks.entity.Rack;
import com.ferdonof.locki.racks.entity.RacksFilter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RackRepositoryPort {
  Optional<Rack> findById(UUID id);

  Rack insert(Rack rack);

  Rack update(Rack rack);

  List<Rack> search(RacksFilter filter);
}
