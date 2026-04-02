package com.ferdonof.locki.lockers.usecases;

import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.entities.UpdateLockerRequest;
import com.ferdonof.locki.lockers.exceptions.LockerNotFoundException;
import com.ferdonof.locki.lockers.ports.LockerRepositoryPort;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class UpdateLockerImpl implements UpdateLocker {

	private final TransactionTemplate transactionTemplate;
	private final LockerRepositoryPort lockerRepository;
	private final RackRepositoryPort rackRepository;

	@Override
	public Locker execute(UpdateLockerRequest request) {
		log.info("Updating locker with id {}", request.id());

		return this.transactionTemplate.execute(status -> {
			final var existingLocker = this.lockerRepository.findById(request.id())
					.orElseThrow(() -> new LockerNotFoundException(request.id()));

			final var rack = Optional.ofNullable(request.rackId())
					.flatMap(this.rackRepository::findById)
					.orElse(existingLocker.rack());

			final var updatedLocker = existingLocker.toBuilder()
					.rack(rack)
					.status(request.status() != null ? request.status() : existingLocker.status())
					.latchStatus(request.latchStatus() != null ? request.latchStatus() : existingLocker.latchStatus())
					.build();

			return this.lockerRepository.update(updatedLocker);
		});
	}
}

