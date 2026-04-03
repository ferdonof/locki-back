package com.ferdonof.locki.lockers.usecases;

import com.ferdonof.locki.lockers.entities.CreateLockerRequest;
import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.enums.LatchStatus;
import com.ferdonof.locki.lockers.enums.LockerStatus;
import com.ferdonof.locki.lockers.ports.LockerRepositoryPort;
import com.ferdonof.locki.racks.entity.Rack;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import static com.ferdonof.locki.racks.enums.RackStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateLockerTest {

	private static final UUID RACK_ID = UUID.randomUUID();
	private static final int LOCKER_NUMBER = 1;

	@Mock
	private TransactionTemplate txTemplate;

	@Mock
	private LockerRepositoryPort lockerRepositoryPort;

	@Mock
	private RackRepositoryPort rackRepositoryPort;

	@Captor
	private ArgumentCaptor<Locker> lockerCaptor;

	@InjectMocks
	private CreateLockerImpl createLocker;

	@BeforeEach
	void setup() {
		when(this.txTemplate.execute(any())).thenAnswer(invocation -> invocation.<TransactionCallback<?>>getArgument(0)
				.doInTransaction(mock(TransactionStatus.class)));
	}

	@Test
	void execute_whenRackExists_shouldCreateLockerWithRack() {
		final Rack rack = Rack.builder().id(RACK_ID).number(1).build();
		final CreateLockerRequest request = CreateLockerRequest.builder().number(LOCKER_NUMBER).rackId(RACK_ID)
				.status(LockerStatus.AVAILABLE).latchStatus(LatchStatus.CLOSED).build();
		final Locker expectedLocker = Locker.builder().id(UUID.randomUUID()).number(LOCKER_NUMBER).rack(rack)
				.status(LockerStatus.AVAILABLE).latchStatus(LatchStatus.CLOSED).version(0L).createdAt(Instant.now())
				.updatedAt(Instant.now()).build();

		when(this.rackRepositoryPort.findById(RACK_ID)).thenReturn(Optional.of(rack));
		when(this.lockerRepositoryPort.insert(any(Locker.class))).thenReturn(expectedLocker);

		final Locker result = this.createLocker.execute(request);

		assertThat(result).isEqualTo(expectedLocker);
		verify(this.rackRepositoryPort).findById(RACK_ID);
		verify(this.lockerRepositoryPort).insert(this.lockerCaptor.capture());

		final Locker capturedLocker = this.lockerCaptor.getValue();
		assertThat(capturedLocker.number()).isEqualTo(LOCKER_NUMBER);
		assertThat(capturedLocker.rack()).isEqualTo(rack);
		assertThat(capturedLocker.status()).isEqualTo(LockerStatus.AVAILABLE);
		assertThat(capturedLocker.latchStatus()).isEqualTo(LatchStatus.CLOSED);
	}

	@Test
	void execute_whenRackDoesNotExist_shouldCreateLockerWithNullRack() {
		final CreateLockerRequest request = CreateLockerRequest.builder().number(LOCKER_NUMBER).rackId(RACK_ID)
				.status(LockerStatus.AVAILABLE).latchStatus(LatchStatus.OPEN).build();
		final Locker expectedLocker = Locker.builder().id(UUID.randomUUID()).number(LOCKER_NUMBER).rack(null)
				.status(LockerStatus.AVAILABLE).latchStatus(LatchStatus.OPEN).version(0L).createdAt(Instant.now())
				.updatedAt(Instant.now()).build();

		when(this.rackRepositoryPort.findById(RACK_ID)).thenReturn(Optional.empty());
		when(this.lockerRepositoryPort.insert(any(Locker.class))).thenReturn(expectedLocker);

		final Locker result = this.createLocker.execute(request);

		assertThat(result).isEqualTo(expectedLocker);
		verify(this.rackRepositoryPort).findById(RACK_ID);
		verify(this.lockerRepositoryPort).insert(this.lockerCaptor.capture());

		final Locker capturedLocker = this.lockerCaptor.getValue();
		assertThat(capturedLocker.rack()).isNull();
	}

	@Test
	void execute_always_shouldRunInsideTransaction() {
		final CreateLockerRequest request = CreateLockerRequest.builder().number(LOCKER_NUMBER).rackId(RACK_ID)
				.status(LockerStatus.AVAILABLE).latchStatus(LatchStatus.CLOSED).build();

		this.createLocker.execute(request);

		verify(this.rackRepositoryPort).findById(any(UUID.class));
		verify(this.lockerRepositoryPort).insert(any(Locker.class));
	}

	@Test
	void execute_whenValidLockerWithRack_shouldCreateLocker() {
		final var rackId = UUID.randomUUID();
		final var lockerId = UUID.randomUUID();

		final var rack = Rack.builder().id(rackId).number(1).status(ACTIVE).build();

		final var request = CreateLockerRequest.builder().number(10).rackId(rackId).status(AVAILABLE)
				.latchStatus(CLOSED).build();

		final var createdLocker = Locker.builder().id(lockerId).number(10).rack(rack).status(AVAILABLE)
				.latchStatus(CLOSED).createdAt(Instant.now()).build();

		when(this.rackRepositoryPort.findById(rackId)).thenReturn(Optional.of(rack));
		when(this.lockerRepositoryPort.insert(any(Locker.class))).thenReturn(createdLocker);

		final var result = this.createLocker.execute(request);

		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(lockerId);
		assertThat(result.number()).isEqualTo(10);
		assertThat(result.status()).isEqualTo(AVAILABLE);
		assertThat(result.latchStatus()).isEqualTo(CLOSED);

		verify(this.rackRepositoryPort).findById(rackId);
		verify(this.lockerRepositoryPort).insert(any(Locker.class));
	}

	@Test
	void execute_whenLockerWithoutRack_shouldCreateLockerWithNullRack() {
		final var lockerId = UUID.randomUUID();

		final var request = CreateLockerRequest.builder().number(10).rackId(null).status(AVAILABLE).latchStatus(CLOSED)
				.build();

		final var createdLocker = Locker.builder().id(lockerId).number(10).rack(null).status(AVAILABLE)
				.latchStatus(CLOSED).createdAt(Instant.now()).build();

		when(this.lockerRepositoryPort.insert(any(Locker.class))).thenReturn(createdLocker);

		final var result = this.createLocker.execute(request);

		assertThat(result).isNotNull();
		assertThat(result.rack()).isNull();
		assertThat(result.number()).isEqualTo(10);

		verify(this.lockerRepositoryPort).insert(any(Locker.class));
	}

}