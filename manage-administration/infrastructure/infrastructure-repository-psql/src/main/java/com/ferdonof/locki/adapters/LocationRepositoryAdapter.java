package com.ferdonof.locki.adapters;

import com.ferdonof.locki.entities.LocationEntity;
import com.ferdonof.locki.locations.entities.Location;
import com.ferdonof.locki.locations.ports.LocationRepositoryPort;
import com.ferdonof.locki.mappers.LocationMapper;
import com.ferdonof.locki.repositories.LocationRepository;
import com.ferdonof.locki.utils.ConstraintViolationHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LocationRepositoryAdapter implements LocationRepositoryPort {

	private final LocationMapper locationMapper;

	private final LocationRepository locationRepository;

	@Override
	public Location insert(Location location) {
		final LocationEntity locationEntity = this.locationMapper.toEntity(location);
		return ConstraintViolationHandler.executeOrThrow(
				() -> this.locationMapper.toDomain(this.locationRepository.saveAndFlush(locationEntity)), Map.of());
	}

	@Override
	public Optional<Location> findById(UUID id) {
		return this.locationRepository.findById(id).map(this.locationMapper::toDomain);
	}
}
