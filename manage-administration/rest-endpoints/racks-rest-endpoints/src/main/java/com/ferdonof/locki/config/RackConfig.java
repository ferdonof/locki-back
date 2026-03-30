package com.ferdonof.locki.config;

import com.ferdonof.locki.locations.ports.LocationRepositoryPort;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;
import com.ferdonof.locki.racks.usecases.CreateRack;
import com.ferdonof.locki.racks.usecases.CreateRackImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class RackConfig {

	@Bean
	public CreateRack createCreateRack(TransactionTemplate transactionTemplate, RackRepositoryPort rackRepositoryPort,
			LocationRepositoryPort locationRepositoryPort) {
		return new CreateRackImpl(transactionTemplate, rackRepositoryPort, locationRepositoryPort);
	}

}
