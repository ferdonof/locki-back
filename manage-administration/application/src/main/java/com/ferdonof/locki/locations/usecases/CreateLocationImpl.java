package com.ferdonof.locki.locations.usecases;

import com.ferdonof.locki.locations.entities.Location;
import com.ferdonof.locki.locations.ports.LocationRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CreateLocationImpl implements CreateLocation {

	private final LocationRepositoryPort locationRepositoryPort;

	@Override
	public Location execute(Location location) {
		log.info("Creating location for code: {}", location.code());
		return this.locationRepositoryPort.insert(location);
	}
}