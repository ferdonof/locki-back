package com.ferdonof.locki.racks.usecases;

import static com.ferdonof.locki.lockers.enums.LatchStatus.LOCKED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.enums.LockerStatus;
import com.ferdonof.locki.lockers.ports.LockerRepositoryPort;
import com.ferdonof.locki.racks.entity.Rack;
import com.ferdonof.locki.racks.enums.RackStatus;
import com.ferdonof.locki.racks.exceptions.RackNotFoundException;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;

@ExtendWith(MockitoExtension.class)
class GetRackLockersImplTest {
  @Mock
  private RackRepositoryPort rackRepository;

  @Mock
  private LockerRepositoryPort lockerRepository;

  @InjectMocks
  private GetRackLockersImpl getRackLockersImpl;

  @Test
  void execute_whenRackHasLockers_shouldReturnLockersList() {
    final var rackId = UUID.randomUUID();

    final var rack = Rack
        .builder()
        .id(rackId)
        .serial(1)
        .status(RackStatus.ACTIVE)
        .build();

    final var locker1 = Locker
        .builder()
        .id(UUID.randomUUID())
        .serial(10)
        .status(LockerStatus.AVAILABLE)
        .latchStatus(LOCKED)
        .createdAt(Instant.now())
        .build();

    final var locker2 = Locker
        .builder()
        .id(UUID.randomUUID())
        .serial(20)
        .status(LockerStatus.RESERVED)
        .latchStatus(LOCKED)
        .createdAt(Instant.now())
        .build();

    final var lockers = List.of(locker1, locker2);

    when(this.rackRepository.findById(rackId)).thenReturn(Optional.of(rack));
    when(this.lockerRepository.findByRackId(rackId)).thenReturn(lockers);

    final var result = this.getRackLockersImpl.execute(rackId);

    assertThat(result)
        .isNotNull()
        .hasSize(2);
    assertThat(result).containsExactlyInAnyOrder(locker1, locker2);

    verify(this.rackRepository).findById(rackId);
    verify(this.lockerRepository).findByRackId(rackId);
  }

  @Test
  void execute_whenRackHasNoLockers_shouldReturnEmptyList() {
    final var rackId = UUID.randomUUID();

    final var rack = Rack
        .builder()
        .id(rackId)
        .serial(1)
        .status(RackStatus.ACTIVE)
        .build();

    when(this.rackRepository.findById(rackId)).thenReturn(Optional.of(rack));
    when(this.lockerRepository.findByRackId(rackId)).thenReturn(List.of());

    final var result = this.getRackLockersImpl.execute(rackId);

    assertThat(result)
        .isNotNull()
        .isEmpty();

    verify(this.rackRepository).findById(rackId);
    verify(this.lockerRepository).findByRackId(rackId);
  }

  @Test
  void execute_whenRackNotFound_shouldThrowRackNotFoundException() {
    final var rackId = UUID.randomUUID();

    when(this.rackRepository.findById(rackId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> this.getRackLockersImpl.execute(rackId))
        .isInstanceOf(RackNotFoundException.class)
        .hasMessage("Rack with id '%s' not found".formatted(rackId));

    verify(this.rackRepository).findById(rackId);
  }
}

