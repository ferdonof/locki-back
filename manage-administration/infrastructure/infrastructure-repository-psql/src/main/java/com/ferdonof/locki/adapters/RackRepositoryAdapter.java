package com.ferdonof.locki.adapters;

import com.ferdonof.locki.mappers.RackMapper;
import com.ferdonof.locki.racks.entity.Rack;
import com.ferdonof.locki.racks.entity.RacksFilter;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;
import com.ferdonof.locki.repositories.RackRepository;
import com.ferdonof.locki.specifications.RackSpecification;
import com.ferdonof.locki.utils.ConstraintViolationHandler;
import com.ferdonof.locki.utils.PaginationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
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

  @Override
  public Rack insert(Rack rack) {
    final var entity = this.rackMapper.toEntity(rack);
    return ConstraintViolationHandler.executeOrThrow(
        () -> this.rackMapper.toDomain(this.rackRepository.save(entity)),
        Map.of()
    );
  }

  @Override
  public Rack update(Rack rack) {
    final var entity = this.rackMapper.toEntity(rack);
    return ConstraintViolationHandler.executeOrThrow(
        () -> this.rackMapper.toDomain(this.rackRepository.saveAndFlush(entity)),
        Map.of()
    );
  }

  @Override
  public List<Rack> search(RacksFilter filter) {
    final var pageable = PaginationValidator.toPageable(filter.offset(), filter.limit());
    final var spec = RackSpecification.fromFilter(filter);
    return this.rackRepository.findAll(spec, pageable)
        .getContent()
        .stream()
        .map(this.rackMapper::toDomain)
        .toList();
  }
}
