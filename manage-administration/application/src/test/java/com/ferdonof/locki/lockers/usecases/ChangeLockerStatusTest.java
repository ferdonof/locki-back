package com.ferdonof.locki.lockers.usecases;

import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.enums.LockerStatus;
import com.ferdonof.locki.lockers.exceptions.LockerNotFoundException;
import com.ferdonof.locki.lockers.ports.LockerRepositoryPort;
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
import static com.ferdonof.locki.lockers.enums.LockerStatus.AVAILABLE;
import static com.ferdonof.locki.lockers.enums.LockerStatus.MAINTENANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeLockerStatusTest {

	@Mock
	private TransactionTemplate txTemplate;

	@Mock
	private LockerRepositoryPort lockerRepository;

	@InjectMocks
	private ChangeLockerStatusImpl changeLockerStatusImpl;

	@BeforeEach
	void setup() {
		when(this.txTemplate.execute(any())).thenAnswer(invocation -> invocation.<TransactionCallback<?>>getArgument(0)
				.doInTransaction(mock(TransactionStatus.class)));
	}

	@Test
	void execute_whenLockerExists_shouldChangeStatus() {
		final var lockerId = UUID.randomUUID();

		final var existingLocker = Locker.builder().id(lockerId).number(10).status(AVAILABLE).latchStatus(CLOSED)
				.createdAt(Instant.now()).build();

		final var updatedLocker = Locker.builder().id(lockerId).number(10).status(MAINTENANCE).latchStatus(CLOSED)
				.createdAt(Instant.now()).build();

		when(this.lockerRepository.findById(lockerId)).thenReturn(Optional.of(existingLocker));
		when(this.lockerRepository.update(any(Locker.class))).thenReturn(updatedLocker);

		final var result = this.changeLockerStatusImpl.execute(lockerId, MAINTENANCE);

		assertThat(result).isNotNull();
		assertThat(result.status()).isEqualTo(MAINTENANCE);
		assertThat(result.latchStatus()).isEqualTo(CLOSED);

		verify(this.lockerRepository).findById(lockerId);
		verify(this.lockerRepository).update(any(Locker.class));
	}

	@Test
	void execute_whenLockerNotFound_shouldThrowLockerNotFoundException() {
		final var lockerId = UUID.randomUUID();

		when(this.lockerRepository.findById(lockerId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> this.changeLockerStatusImpl.execute(lockerId, MAINTENANCE))
				.isInstanceOf(LockerNotFoundException.class)
				.hasMessage("Locker with id '%s' not found".formatted(lockerId));

		verify(this.lockerRepository).findById(lockerId);
	}

	@Test
	void execute_withDifferentStatuses_shouldChangeToEachStatus() {
		final var lockerId = UUID.randomUUID();

		final var existingLocker = Locker.builder().id(lockerId).number(10).status(AVAILABLE).latchStatus(CLOSED)
				.createdAt(Instant.now()).build();

		when(this.lockerRepository.findById(lockerId)).thenReturn(Optional.of(existingLocker));

		for (final LockerStatus status : LockerStatus.values()) {
			final var updatedLocker = existingLocker.toBuilder().status(status).build();
			when(this.lockerRepository.update(any(Locker.class))).thenReturn(updatedLocker);

			final var result = this.changeLockerStatusImpl.execute(lockerId, status);

			assertThat(result.status()).isEqualTo(status);
		}

		verify(this.lockerRepository, times(LockerStatus.values().length)).findById(lockerId);
		verify(this.lockerRepository, times(LockerStatus.values().length)).update(any(Locker.class));
	}
}
