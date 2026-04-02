package com.ferdonof.locki.config;

import com.ferdonof.locki.lockers.ports.LockerRepositoryPort;
import com.ferdonof.locki.lockers.usecases.ChangeLatchStatus;
import com.ferdonof.locki.lockers.usecases.ChangeLatchStatusImpl;
import com.ferdonof.locki.lockers.usecases.ChangeLockerStatus;
import com.ferdonof.locki.lockers.usecases.ChangeLockerStatusImpl;
import com.ferdonof.locki.lockers.usecases.CreateLocker;
import com.ferdonof.locki.lockers.usecases.CreateLockerImpl;
import com.ferdonof.locki.lockers.usecases.GetLocker;
import com.ferdonof.locki.lockers.usecases.GetLockerImpl;
import com.ferdonof.locki.lockers.usecases.SearchLockers;
import com.ferdonof.locki.lockers.usecases.SearchLockersImpl;
import com.ferdonof.locki.lockers.usecases.UpdateLocker;
import com.ferdonof.locki.lockers.usecases.UpdateLockerImpl;
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

  @Bean
  public GetLocker getLocker(final LockerRepositoryPort lockerRepositoryPort) {
    return new GetLockerImpl(lockerRepositoryPort);
  }

  @Bean
  public UpdateLocker updateLocker(final TransactionTemplate transactionTemplate,
                                   final LockerRepositoryPort lockerRepositoryPort,
                                   final RackRepositoryPort rackRepositoryPort) {
    return new UpdateLockerImpl(transactionTemplate, lockerRepositoryPort, rackRepositoryPort);
  }

  @Bean
  public SearchLockers searchLockers(final LockerRepositoryPort lockerRepositoryPort) {
    return new SearchLockersImpl(lockerRepositoryPort);
  }

  @Bean
  public ChangeLockerStatus changeLockerStatus(final LockerRepositoryPort lockerRepositoryPort) {
    return new ChangeLockerStatusImpl(lockerRepositoryPort);
  }

  @Bean
  public ChangeLatchStatus changeLatchStatus(final LockerRepositoryPort lockerRepositoryPort) {
    return new ChangeLatchStatusImpl(lockerRepositoryPort);
  }
}
