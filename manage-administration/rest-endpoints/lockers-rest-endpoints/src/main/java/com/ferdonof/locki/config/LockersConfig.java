package com.ferdonof.locki.config;

import com.ferdonof.locki.lockers.ports.LockerRepositoryPort;
import com.ferdonof.locki.lockers.usecases.CreateLocker;
import com.ferdonof.locki.lockers.usecases.CreateLockerImpl;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class LockersConfig {

  @Bean
  public CreateLocker createLocker(final TransactionTemplate transactionTemplate,
                                   final LockerRepositoryPort lockerRepositoryPort,
                                   final RackRepositoryPort rackRepositoryPort) {
    return new CreateLockerImpl(transactionTemplate, lockerRepositoryPort, rackRepositoryPort);
  }
}
