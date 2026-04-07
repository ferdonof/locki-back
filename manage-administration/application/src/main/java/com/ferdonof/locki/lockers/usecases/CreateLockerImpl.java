package com.ferdonof.locki.lockers.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.springframework.transaction.support.TransactionTemplate;

import com.ferdonof.locki.lockers.entities.CreateLockerRequest;
import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.ports.LockerRepositoryPort;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;

@Slf4j
@RequiredArgsConstructor
public class CreateLockerImpl implements CreateLocker {
  private final TransactionTemplate transactionTemplate;

  private final LockerRepositoryPort lockerRepositoryPort;

  private final RackRepositoryPort rackRepositoryPort;

  @Override
  public Locker execute(CreateLockerRequest request) {
    log.info("Creating locker with serial number {} in rack {}", request.serial(), request.rackId());

    return this.transactionTemplate.execute(status ->
    {
      final var rack = Optional
          .ofNullable(request.rackId())
          .flatMap(this.rackRepositoryPort::findById)
          .orElse(null);

      final var locker = Locker
          .builder()
          .serial(request.serial())
          .rack(rack)
          .status(request.status())
          .latchStatus(request.latchStatus())
          .build();

      return this.lockerRepositoryPort.insert(locker);
    });
  }
}
