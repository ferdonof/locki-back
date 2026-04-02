package com.ferdonof.locki.lockers.usecases;

import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.exceptions.LockerNotFoundException;
import com.ferdonof.locki.lockers.ports.LockerRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class GetLockerImpl implements GetLocker {

	private final LockerRepositoryPort lockerRepository;

	@Override
	public Locker execute(UUID id) {
		log.info("Getting locker with id {}", id);
		return this.lockerRepository.findById(id)
				.orElseThrow(() -> new LockerNotFoundException(id));
	}
}

