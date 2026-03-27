package com.ferdonof.locki.locations.config;

import com.ferdonof.locki.locations.ports.LocationRepositoryPort;
import com.ferdonof.locki.locations.usecases.CreateLocation;
import com.ferdonof.locki.locations.usecases.CreateLocationImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocationConfig {

  @Bean
  public CreateLocation createLocation(LocationRepositoryPort locationRepositoryPort) {
    return new CreateLocationImpl(locationRepositoryPort);
  }
}
