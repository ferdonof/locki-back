package com.ferdonof.locki.lockers.usecases;

import static com.ferdonof.locki.lockers.enums.LatchStatus.LOCKED;
import static com.ferdonof.locki.lockers.enums.LockerStatus.AVAILABLE;
import static com.ferdonof.locki.lockers.enums.LockerStatus.RESERVED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.entities.LockersFilter;
import com.ferdonof.locki.lockers.ports.LockerRepositoryPort;

@ExtendWith(MockitoExtension.class)
class SearchLockersTest {
  @Mock
  private LockerRepositoryPort lockerRepository;

  @InjectMocks
  private SearchLockersImpl searchLockersImpl;

  @Test
  void execute_whenLockersExist_shouldReturnList() {
    final var locker1 = Locker
        .builder()
        .id(UUID.randomUUID())
        .serial(10)
        .status(AVAILABLE)
        .latchStatus(LOCKED)
        .createdAt(Instant.now())
        .build();

    final var locker2 = Locker
        .builder()
        .id(UUID.randomUUID())
        .serial(20)
        .status(RESERVED)
        .latchStatus(LOCKED)
        .createdAt(Instant.now())
        .build();

    final var filter = LockersFilter
        .builder()
        .limit(10)
        .offset(0)
        .build();
    final var lockers = List.of(locker1, locker2);

    when(this.lockerRepository.search(any(LockersFilter.class))).thenReturn(lockers);

    final var result = this.searchLockersImpl.execute(filter);

    assertThat(result)
        .isNotNull()
        .hasSize(2);
    assertThat(result).containsExactlyInAnyOrder(locker1, locker2);

    verify(this.lockerRepository).search(any(LockersFilter.class));
  }

  @Test
  void execute_whenNoLockersExist_shouldReturnEmptyList() {
    final var filter = LockersFilter
        .builder()
        .limit(10)
        .offset(0)
        .build();

    when(this.lockerRepository.search(any(LockersFilter.class))).thenReturn(List.of());

    final var result = this.searchLockersImpl.execute(filter);

    assertThat(result)
        .isNotNull()
        .isEmpty();

    verify(this.lockerRepository).search(any(LockersFilter.class));
  }

  @Test
  void execute_withFilterByStatus_shouldReturnFilteredResults() {
    final var lockers = List
        .of(Locker
            .builder()
            .id(UUID.randomUUID())
            .serial(10)
            .status(AVAILABLE)
            .latchStatus(LOCKED)
            .build());

    final var filter = LockersFilter
        .builder()
        .status(AVAILABLE)
        .limit(10)
        .offset(0)
        .build();

    when(this.lockerRepository.search(filter)).thenReturn(lockers);

    final var result = this.searchLockersImpl.execute(filter);

    assertThat(result).hasSize(1);
    assertThat(result
        .getFirst()
        .status()).isEqualTo(AVAILABLE);

    verify(this.lockerRepository).search(filter);
  }
}
