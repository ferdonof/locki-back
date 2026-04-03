package com.ferdonof.locki.locations.config;

import com.ferdonof.locki.locations.ports.LocationRepositoryPort;
import com.ferdonof.locki.locations.usecases.CreateLocation;
import com.ferdonof.locki.locations.usecases.CreateLocationImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class LocationConfig {

	@Bean
	public CreateLocation createLocation(TransactionTemplate transactionTemplate,
			LocationRepositoryPort locationRepositoryPort) {
		return new CreateLocationImpl(transactionTemplate, locationRepositoryPort);
	}
}
