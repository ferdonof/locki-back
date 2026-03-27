package com.ferdonof.locki.adapters;

import com.ferdonof.locki.mappers.RackMapper;
import com.ferdonof.locki.racks.entity.Rack;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;
import com.ferdonof.locki.repositories.RackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RackRepositoryAdapter implements RackRepositoryPort {

  private final RackMapper rackMapper;

  private final RackRepository rackRepository;

  @Override
  public Optional<Rack> findById(UUID id) {
    return this.rackRepository.findById(id).map(this.rackMapper::toDomain);
  }
}
