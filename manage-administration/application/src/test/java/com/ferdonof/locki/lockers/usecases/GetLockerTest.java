package com.ferdonof.locki.lockers.usecases;

import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.exceptions.LockerNotFoundException;
import com.ferdonof.locki.lockers.ports.LockerRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.ferdonof.locki.lockers.enums.LatchStatus.CLOSED;
import static com.ferdonof.locki.lockers.enums.LockerStatus.AVAILABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetLockerTest {

	@Mock
	private LockerRepositoryPort lockerRepository;

	@InjectMocks
	private GetLockerImpl getLockerImpl;

	@Test
	void execute_whenLockerExists_shouldReturnLocker() {
		final var lockerId = UUID.randomUUID();
		final var locker = Locker.builder().id(lockerId).number(10).status(AVAILABLE).latchStatus(CLOSED)
				.createdAt(Instant.now()).build();

		when(this.lockerRepository.findById(lockerId)).thenReturn(Optional.of(locker));

		final var result = this.getLockerImpl.execute(lockerId);

		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(lockerId);
		assertThat(result.number()).isEqualTo(10);
		assertThat(result.status()).isEqualTo(AVAILABLE);
		assertThat(result.latchStatus()).isEqualTo(CLOSED);

		verify(this.lockerRepository).findById(lockerId);
	}

	@Test
	void execute_whenLockerNotExists_shouldThrowLockerNotFoundException() {
		final var lockerId = UUID.randomUUID();

		when(this.lockerRepository.findById(lockerId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> this.getLockerImpl.execute(lockerId)).isInstanceOf(LockerNotFoundException.class)
				.hasMessage("Locker with id '%s' not found".formatted(lockerId));

		verify(this.lockerRepository).findById(lockerId);
	}
}
