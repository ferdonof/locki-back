package com.ferdonof.locki.lockers.usecases;

import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.enums.LockerStatus;
import com.ferdonof.locki.lockers.exceptions.LockerNotFoundException;
import com.ferdonof.locki.lockers.ports.LockerRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class ChangeLockerStatusImpl implements ChangeLockerStatus {

	private final TransactionTemplate transactionTemplate;

	private final LockerRepositoryPort lockerRepository;

	@Override
	public Locker execute(UUID id, LockerStatus status) {
		log.info("Changing locker {} status to {}", id, status);

		return this.transactionTemplate.execute(txStatus -> {
			final var locker = this.lockerRepository.findById(id).orElseThrow(() -> new LockerNotFoundException(id));

			final var updatedLocker = locker.toBuilder().status(status).build();

			return this.lockerRepository.update(updatedLocker);
		});
	}
}
