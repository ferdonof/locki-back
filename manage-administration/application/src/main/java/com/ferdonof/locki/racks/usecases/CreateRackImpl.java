package com.ferdonof.locki.racks.usecases;

import com.ferdonof.locki.locations.ports.LocationRepositoryPort;
import com.ferdonof.locki.racks.entity.CreateRackRequest;
import com.ferdonof.locki.racks.entity.Rack;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class CreateRackImpl implements CreateRack {

  private final TransactionTemplate transactionTemplate;

  private final RackRepositoryPort rackRepositoryPort;

  private final LocationRepositoryPort locationRepositoryPort;

  @Override
  public Rack execute(CreateRackRequest rack) {
    log.info("Creating rack with number {} in location {}", rack.number(), rack.locationId());

    return this.transactionTemplate.execute(status -> {
      final var location =
          Optional.ofNullable(rack.locationId())
           .flatMap(this.locationRepositoryPort::findById)
           .orElse(null);

      final var newRack = Rack.builder()
          .number(rack.number())
          .status(rack.status())
          .location(location)
          .build();

      return this.rackRepositoryPort.insert(newRack);

    });

  }
}
