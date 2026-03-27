package com.ferdonof.locki.lockers.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ferdonof.locki.lockers.entities.CreateLockerRequest;
import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.enums.LatchStatus;
import com.ferdonof.locki.lockers.enums.LockerStatus;
import com.ferdonof.locki.lockers.ports.LockerRepositoryPort;
import com.ferdonof.locki.racks.entity.Rack;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;

@ExtendWith(MockitoExtension.class)
class CreateLockerTest {

	private static final UUID RACK_ID = UUID.randomUUID();
	private static final int LOCKER_NUMBER = 1;

	@Mock
	private TransactionTemplate transactionTemplate;

	@Mock
	private LockerRepositoryPort lockerRepositoryPort;

	@Mock
	private RackRepositoryPort rackRepositoryPort;

	@Mock
	private TransactionStatus transactionStatus;

	@Captor
	private ArgumentCaptor<Locker> lockerCaptor;

	private CreateLockerImpl createLocker;

	@BeforeEach
	void setUp() {
		this.createLocker = new CreateLockerImpl(this.transactionTemplate, this.lockerRepositoryPort, this.rackRepositoryPort);
	}

	@Test
	void execute_whenRackExists_shouldCreateLockerWithRack() {
		final Rack rack = Rack.builder().id(RACK_ID).number(1).build();
		final CreateLockerRequest request = CreateLockerRequest.builder()
				.number(LOCKER_NUMBER)
				.rackId(RACK_ID)
				.status(LockerStatus.AVAILABLE)
				.latchStatus(LatchStatus.CLOSED)
				.build();
		final Locker expectedLocker = Locker.builder()
				.id(UUID.randomUUID())
				.number(LOCKER_NUMBER)
				.rack(rack)
				.status(LockerStatus.AVAILABLE)
				.latchStatus(LatchStatus.CLOSED)
				.version(0L)
				.createdAt(Instant.now())
				.updatedAt(Instant.now())
				.build();

		when(this.transactionTemplate.execute(any())).thenAnswer(invocation -> {
			final TransactionCallback<?> callback = invocation.getArgument(0);
			return callback.doInTransaction(this.transactionStatus);
		});
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
		final CreateLockerRequest request = CreateLockerRequest.builder()
				.number(LOCKER_NUMBER)
				.rackId(RACK_ID)
				.status(LockerStatus.AVAILABLE)
				.latchStatus(LatchStatus.OPEN)
				.build();
		final Locker expectedLocker = Locker.builder()
				.id(UUID.randomUUID())
				.number(LOCKER_NUMBER)
				.rack(null)
				.status(LockerStatus.AVAILABLE)
				.latchStatus(LatchStatus.OPEN)
				.version(0L)
				.createdAt(Instant.now())
				.updatedAt(Instant.now())
				.build();

		when(this.transactionTemplate.execute(any())).thenAnswer(invocation -> {
			final TransactionCallback<?> callback = invocation.getArgument(0);
			return callback.doInTransaction(this.transactionStatus);
		});
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
		final CreateLockerRequest request = CreateLockerRequest.builder()
				.number(LOCKER_NUMBER)
				.rackId(RACK_ID)
				.status(LockerStatus.AVAILABLE)
				.latchStatus(LatchStatus.CLOSED)
				.build();

		when(this.transactionTemplate.execute(any())).thenReturn(null);

		this.createLocker.execute(request);

		verify(this.transactionTemplate).execute(any());
		verifyNoInteractions(this.rackRepositoryPort);
		verifyNoInteractions(this.lockerRepositoryPort);
	}
}