package com.ferdonof.locki.lockers.usecases;

import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.enums.LatchStatus;
import com.ferdonof.locki.lockers.exceptions.LockerNotFoundException;
import com.ferdonof.locki.lockers.ports.LockerRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class ChangeLatchStatusImpl implements ChangeLatchStatus {

	private final LockerRepositoryPort lockerRepository;

	@Override
	public Locker execute(UUID id, LatchStatus status) {
		log.info("Changing latch status for locker {} to {}", id, status);

		final var locker = this.lockerRepository.findById(id)
				.orElseThrow(() -> new LockerNotFoundException(id));

		final var updatedLocker = locker.toBuilder()
				.latchStatus(status)
				.build();

		return this.lockerRepository.update(updatedLocker);
	}
}

