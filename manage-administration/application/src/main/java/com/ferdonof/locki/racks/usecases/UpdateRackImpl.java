package com.ferdonof.locki.racks.usecases;

import com.ferdonof.locki.racks.entity.Rack;
import com.ferdonof.locki.racks.entity.UpdateRackRequest;
import com.ferdonof.locki.racks.exceptions.RackNotFoundException;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UpdateRackImpl implements UpdateRack {

	private final RackRepositoryPort rackRepository;

	@Override
	public Rack execute(UpdateRackRequest request) {
		log.info("Updating rack with id {}", request.id());

		final var existingRack = this.rackRepository.findById(request.id())
				.orElseThrow(() -> new RackNotFoundException(request.id()));

		final var updatedRack = existingRack.toBuilder()
				.status(request.status() != null ? request.status() : existingRack.status())
				.location(request.location() != null ? request.location() : existingRack.location())
				.build();

		return this.rackRepository.update(updatedRack);
	}
}

