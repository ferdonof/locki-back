package com.ferdonof.locki.racks.usecases;

import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.ports.LockerRepositoryPort;
import com.ferdonof.locki.racks.exceptions.RackNotFoundException;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class GetRackLockersImpl implements GetRackLockers {

	private final RackRepositoryPort rackRepository;
	private final LockerRepositoryPort lockerRepository;

	@Override
	public List<Locker> execute(UUID rackId) {
		log.info("Getting lockers for rack with id {}", rackId);

		this.rackRepository.findById(rackId)
				.orElseThrow(() -> new RackNotFoundException(rackId));

		return this.lockerRepository.findByRackId(rackId);
	}
}

