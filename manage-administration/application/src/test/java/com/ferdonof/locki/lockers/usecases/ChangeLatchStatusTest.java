package com.ferdonof.locki.lockers.usecases;

import static com.ferdonof.locki.lockers.enums.LatchStatus.UNLOCKED;
import static com.ferdonof.locki.lockers.enums.LockerStatus.AVAILABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.enums.LatchStatus;
import com.ferdonof.locki.lockers.exceptions.LockerNotFoundException;
import com.ferdonof.locki.lockers.ports.LockerRepositoryPort;

@ExtendWith(MockitoExtension.class)
class ChangeLatchStatusTest {
  @Mock
  private LockerRepositoryPort lockerRepository;

  @Mock
  private TransactionTemplate txTemplate;

  @InjectMocks
  private ChangeLatchStatusImpl changeLatchStatusImpl;

  @BeforeEach
  void setup() {
    when(this.txTemplate.execute(any())).thenAnswer(invocation -> invocation
        .<TransactionCallback<?>>getArgument(0)
        .doInTransaction(mock(TransactionStatus.class)));
  }

  @Test
  void execute_whenLockerExists_shouldChangeLatchStatus() {
    final var lockerId = UUID.randomUUID();

    final var existingLocker = Locker
        .builder()
        .id(lockerId)
        .serial(10)
        .status(AVAILABLE)
        .latchStatus(UNLOCKED)
        .createdAt(Instant.now())
        .build();

    final var updatedLocker = Locker
        .builder()
        .id(lockerId)
        .serial(10)
        .status(AVAILABLE)
        .latchStatus(UNLOCKED)
        .createdAt(Instant.now())
        .build();

    when(this.lockerRepository.findById(lockerId)).thenReturn(Optional.of(existingLocker));
    when(this.lockerRepository.update(any(Locker.class))).thenReturn(updatedLocker);

    final var result = this.changeLatchStatusImpl.execute(lockerId, UNLOCKED);

    assertThat(result).isNotNull();
    assertThat(result.latchStatus()).isEqualTo(UNLOCKED);
    assertThat(result.status()).isEqualTo(AVAILABLE);

    verify(this.lockerRepository).findById(lockerId);
    verify(this.lockerRepository).update(any(Locker.class));
  }

  @Test
  void execute_whenLockerNotFound_shouldThrowLockerNotFoundException() {
    final var lockerId = UUID.randomUUID();

    when(this.lockerRepository.findById(lockerId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> this.changeLatchStatusImpl.execute(lockerId, UNLOCKED))
        .isInstanceOf(LockerNotFoundException.class)
        .hasMessage("Locker with id '%s' not found".formatted(lockerId));

    verify(this.lockerRepository).findById(lockerId);
  }

  @Test
  void execute_withDifferentLatchStatuses_shouldChangeToEachStatus() {
    final var lockerId = UUID.randomUUID();

    final var existingLocker = Locker
        .builder()
        .id(lockerId)
        .serial(10)
        .status(AVAILABLE)
        .latchStatus(UNLOCKED)
        .createdAt(Instant.now())
        .build();

    when(this.lockerRepository.findById(lockerId)).thenReturn(Optional.of(existingLocker));

    for (final LatchStatus status : LatchStatus.values()) {
      final var updatedLocker = existingLocker
          .toBuilder()
          .latchStatus(status)
          .build();
      when(this.lockerRepository.update(any(Locker.class))).thenReturn(updatedLocker);

      final var result = this.changeLatchStatusImpl.execute(lockerId, status);

      assertThat(result.latchStatus()).isEqualTo(status);
    }

    verify(this.lockerRepository, times(LatchStatus.values().length)).findById(lockerId);
    verify(this.lockerRepository, times(LatchStatus.values().length)).update(any(Locker.class));
  }
}
