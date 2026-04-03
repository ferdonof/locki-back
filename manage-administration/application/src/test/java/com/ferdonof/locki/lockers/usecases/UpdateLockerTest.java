package com.ferdonof.locki.lockers.usecases;

import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.entities.UpdateLockerRequest;
import com.ferdonof.locki.lockers.exceptions.LockerNotFoundException;
import com.ferdonof.locki.lockers.ports.LockerRepositoryPort;
import com.ferdonof.locki.racks.entity.Rack;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.ferdonof.locki.lockers.enums.LatchStatus.CLOSED;
import static com.ferdonof.locki.lockers.enums.LatchStatus.LOCKED;
import static com.ferdonof.locki.lockers.enums.LockerStatus.AVAILABLE;
import static com.ferdonof.locki.lockers.enums.LockerStatus.MAINTENANCE;
import static com.ferdonof.locki.racks.enums.RackStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateLockerTest {

	@Mock
	private TransactionTemplate txTemplate;

	@Mock
	private LockerRepositoryPort lockerRepository;

	@Mock
	private RackRepositoryPort rackRepository;

	@InjectMocks
	private UpdateLockerImpl updateLockerImpl;

	@BeforeEach
	void setup() {
		when(this.txTemplate.execute(any())).thenAnswer(invocation -> invocation.<TransactionCallback<?>>getArgument(0)
				.doInTransaction(mock(TransactionStatus.class)));
	}

	@Test
	void execute_whenValidUpdate_shouldUpdateLocker() {
		final var lockerId = UUID.randomUUID();
		final var rackId = UUID.randomUUID();

		final var rack = Rack.builder().id(rackId).number(2).status(ACTIVE).build();

		final var existingLocker = Locker.builder().id(lockerId).number(10).status(AVAILABLE).latchStatus(CLOSED)
				.createdAt(Instant.now()).build();

		final var updateRequest = UpdateLockerRequest.builder().id(lockerId).rackId(rackId).status(MAINTENANCE)
				.latchStatus(LOCKED).build();

		final var updatedLocker = Locker.builder().id(lockerId).number(10).rack(rack).status(MAINTENANCE)
				.latchStatus(LOCKED).createdAt(Instant.now()).build();

		when(this.lockerRepository.findById(lockerId)).thenReturn(Optional.of(existingLocker));
		when(this.rackRepository.findById(rackId)).thenReturn(Optional.of(rack));
		when(this.lockerRepository.update(any(Locker.class))).thenReturn(updatedLocker);

		final var result = this.updateLockerImpl.execute(updateRequest);

		assertThat(result).isNotNull();
		assertThat(result.status()).isEqualTo(MAINTENANCE);
		assertThat(result.latchStatus()).isEqualTo(LOCKED);

		verify(this.lockerRepository).findById(lockerId);
		verify(this.rackRepository).findById(rackId);
		verify(this.lockerRepository).update(any(Locker.class));
	}

	@Test
	void execute_whenLockerNotFound_shouldThrowLockerNotFoundException() {
		final var lockerId = UUID.randomUUID();

		final var updateRequest = UpdateLockerRequest.builder().id(lockerId).status(MAINTENANCE).latchStatus(LOCKED)
				.build();

		when(this.lockerRepository.findById(lockerId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> this.updateLockerImpl.execute(updateRequest))
				.isInstanceOf(LockerNotFoundException.class)
				.hasMessage("Locker with id '%s' not found".formatted(lockerId));

		verify(this.lockerRepository).findById(lockerId);
	}

	@Test
	void execute_whenPartialUpdate_shouldPreserveExistingValues() {
		final var lockerId = UUID.randomUUID();

		final var existingLocker = Locker.builder().id(lockerId).number(10).status(AVAILABLE).latchStatus(CLOSED)
				.createdAt(Instant.now()).build();

		final var updateRequest = UpdateLockerRequest.builder().id(lockerId).status(null).latchStatus(LOCKED).build();

		final var updatedLocker = Locker.builder().id(lockerId).number(10).status(AVAILABLE).latchStatus(LOCKED)
				.createdAt(Instant.now()).build();

		when(this.lockerRepository.findById(lockerId)).thenReturn(Optional.of(existingLocker));
		when(this.lockerRepository.update(any(Locker.class))).thenReturn(updatedLocker);

		final var result = this.updateLockerImpl.execute(updateRequest);

		assertThat(result.status()).isEqualTo(AVAILABLE);
		assertThat(result.latchStatus()).isEqualTo(LOCKED);

		verify(this.lockerRepository).update(any(Locker.class));
	}

	@Test
	void execute_whenRackNotFoundAndRackIdProvided_shouldUseExistingRack() {
		final var lockerId = UUID.randomUUID();
		final var rackId = UUID.randomUUID();

		final var existingRack = Rack.builder().id(UUID.randomUUID()).number(1).status(ACTIVE).build();

		final var existingLocker = Locker.builder().id(lockerId).number(10).rack(existingRack).status(AVAILABLE)
				.latchStatus(CLOSED).createdAt(Instant.now()).build();

		final var updateRequest = UpdateLockerRequest.builder().id(lockerId).rackId(rackId).status(MAINTENANCE)
				.latchStatus(LOCKED).build();

		final var updatedLocker = Locker.builder().id(lockerId).number(10).rack(existingRack).status(MAINTENANCE)
				.latchStatus(LOCKED).createdAt(Instant.now()).build();

		when(this.lockerRepository.findById(lockerId)).thenReturn(Optional.of(existingLocker));
		when(this.rackRepository.findById(rackId)).thenReturn(Optional.empty());
		when(this.lockerRepository.update(any(Locker.class))).thenReturn(updatedLocker);

		final var result = this.updateLockerImpl.execute(updateRequest);

		assertThat(result.rack()).isEqualTo(existingRack);
		assertThat(result.status()).isEqualTo(MAINTENANCE);

		verify(this.lockerRepository).update(any(Locker.class));
	}
}
