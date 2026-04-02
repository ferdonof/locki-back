package com.ferdonof.locki.racks.usecases;

import com.ferdonof.locki.racks.entity.Rack;
import com.ferdonof.locki.racks.exceptions.RackNotFoundException;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class GetRackImpl implements GetRack {

	private final RackRepositoryPort rackRepository;

	@Override
	public Rack execute(UUID id) {
		log.info("Getting rack with id {}", id);
		return this.rackRepository.findById(id)
				.orElseThrow(() -> new RackNotFoundException(id));
	}
}

