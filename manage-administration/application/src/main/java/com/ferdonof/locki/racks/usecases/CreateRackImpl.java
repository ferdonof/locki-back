package com.ferdonof.locki.racks.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.springframework.transaction.support.TransactionTemplate;

import com.ferdonof.locki.locations.ports.LocationRepositoryPort;
import com.ferdonof.locki.racks.entity.CreateRackRequest;
import com.ferdonof.locki.racks.entity.Rack;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;

@Slf4j
@RequiredArgsConstructor
public class CreateRackImpl implements CreateRack {
  private final TransactionTemplate transactionTemplate;

  private final RackRepositoryPort rackRepositoryPort;

  private final LocationRepositoryPort locationRepositoryPort;

  @Override
  public Rack execute(CreateRackRequest rack) {
    log.info("Creating rack with serial number {} in location {}", rack.serial(), rack.locationId());

    return this.transactionTemplate.execute(status ->
    {
      final var location =
          Optional
              .ofNullable(rack.locationId())
              .flatMap(this.locationRepositoryPort::findById)
              .orElse(null);

      final var newRack = Rack
          .builder()
          .serial(rack.serial())
          .size(rack.size())
          .status(rack.status())
          .location(location)
          .build();

      return this.rackRepositoryPort.insert(newRack);
    });
  }
}
