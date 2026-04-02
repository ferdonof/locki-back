package com.ferdonof.locki.config;

import com.ferdonof.locki.locations.ports.LocationRepositoryPort;
import com.ferdonof.locki.lockers.ports.LockerRepositoryPort;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;
import com.ferdonof.locki.racks.usecases.CreateRack;
import com.ferdonof.locki.racks.usecases.CreateRackImpl;
import com.ferdonof.locki.racks.usecases.GetRack;
import com.ferdonof.locki.racks.usecases.GetRackImpl;
import com.ferdonof.locki.racks.usecases.GetRackLockers;
import com.ferdonof.locki.racks.usecases.GetRackLockersImpl;
import com.ferdonof.locki.racks.usecases.SearchRacks;
import com.ferdonof.locki.racks.usecases.SearchRacksImpl;
import com.ferdonof.locki.racks.usecases.UpdateRack;
import com.ferdonof.locki.racks.usecases.UpdateRackImpl;
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

	@Bean
	public GetRack getRack(RackRepositoryPort rackRepositoryPort) {
		return new GetRackImpl(rackRepositoryPort);
	}

	@Bean
	public UpdateRack updateRack(RackRepositoryPort rackRepositoryPort) {
		return new UpdateRackImpl(rackRepositoryPort);
	}

	@Bean
	public SearchRacks searchRacks(RackRepositoryPort rackRepositoryPort) {
		return new SearchRacksImpl(rackRepositoryPort);
	}

	@Bean
	public GetRackLockers getRackLockers(RackRepositoryPort rackRepositoryPort,
			LockerRepositoryPort lockerRepositoryPort) {
		return new GetRackLockersImpl(rackRepositoryPort, lockerRepositoryPort);
	}
}
