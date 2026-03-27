package com.ferdonof.locki.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ferdonof.locki.entities.LockerEntity;
import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.enums.LatchStatus;
import com.ferdonof.locki.lockers.enums.LockerStatus;
import com.ferdonof.locki.mappers.LockerMapper;
import com.ferdonof.locki.repositories.LockerRepository;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class LockerRepositoryAdapterTest {

	@Mock
	private LockerMapper lockerMapper;

	@Mock
	private LockerRepository lockerRepository;

	@InjectMocks
	private LockerRepositoryAdapter lockerRepositoryAdapter;

	@Test
	void insert_whenValidLocker_shouldReturnSavedLocker() {
		final Locker locker = Locker.builder()
				.number(1)
				.status(LockerStatus.AVAILABLE)
				.latchStatus(LatchStatus.CLOSED)
				.build();
		final LockerEntity entity = LockerEntity.builder()
				.id(UUID.randomUUID())
				.number(1)
				.status(LockerStatus.AVAILABLE)
				.latchStatus(LatchStatus.CLOSED)
				.version(0L)
				.createdAt(Instant.now())
				.updatedAt(Instant.now())
				.build();
		final Locker expectedLocker = Locker.builder()
				.id(entity.getId())
				.number(1)
				.status(LockerStatus.AVAILABLE)
				.latchStatus(LatchStatus.CLOSED)
				.version(0L)
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.build();

		when(this.lockerMapper.toEntity(locker)).thenReturn(entity);
		when(this.lockerRepository.save(entity)).thenReturn(entity);
		when(this.lockerMapper.toDomain(entity)).thenReturn(expectedLocker);

		final Locker result = this.lockerRepositoryAdapter.insert(locker);

		assertThat(result).isEqualTo(expectedLocker);
		assertThat(result.id()).isNotNull();
		assertThat(result.number()).isEqualTo(1);
		assertThat(result.status()).isEqualTo(LockerStatus.AVAILABLE);
		assertThat(result.latchStatus()).isEqualTo(LatchStatus.CLOSED);
		verify(this.lockerMapper).toEntity(locker);
		verify(this.lockerRepository).save(entity);
		verify(this.lockerMapper).toDomain(entity);
	}
}

