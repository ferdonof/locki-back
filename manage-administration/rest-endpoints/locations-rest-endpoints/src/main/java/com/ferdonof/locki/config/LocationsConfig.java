package com.ferdonof.locki.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

import com.ferdonof.locki.locations.ports.LocationRepositoryPort;
import com.ferdonof.locki.locations.usecases.CreateLocation;
import com.ferdonof.locki.locations.usecases.CreateLocationImpl;

@Configuration
public class LocationsConfig {

  @Bean
  public CreateLocation createLocation(final TransactionTemplate transactionTemplate,
      final LocationRepositoryPort locationRepositoryPort) {
    return new CreateLocationImpl(transactionTemplate, locationRepositoryPort);
  }
}
