package com.ferdonof.locki.racks.usecases;

import static com.ferdonof.locki.racks.enums.RackStatus.ACTIVE;
import static com.ferdonof.locki.racks.enums.RackStatus.MAINTENANCE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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

import com.ferdonof.locki.racks.entity.Rack;
import com.ferdonof.locki.racks.entity.UpdateRackRequest;
import com.ferdonof.locki.racks.exceptions.RackNotFoundException;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;

@ExtendWith(MockitoExtension.class)
class UpdateRackTest {

  @Mock
  private TransactionTemplate txTemplate;

  @Mock
  private RackRepositoryPort rackRepository;

  @InjectMocks
  private UpdateRackImpl updateRackImpl;

  @BeforeEach
  void setup() {
    when(this.txTemplate.execute(any())).thenAnswer(invocation -> invocation
        .<TransactionCallback<?>>getArgument(0)
        .doInTransaction(mock(TransactionStatus.class)));
  }

  @Test
  void execute_whenValidUpdate_shouldUpdateRack() {
    final var rackId = UUID.randomUUID();

    final var existingRack = Rack
        .builder()
        .id(rackId)
        .serial(1)
        .status(ACTIVE)
        .createdAt(Instant.now())
        .build();

    final var updateRequest = UpdateRackRequest
        .builder()
        .id(rackId)
        .status(MAINTENANCE)
        .build();

    final var updatedRack = Rack
        .builder()
        .id(rackId)
        .serial(1)
        .status(MAINTENANCE)
        .createdAt(Instant.now())
        .build();

    when(this.rackRepository.findById(rackId)).thenReturn(Optional.of(existingRack));
    when(this.rackRepository.update(any(Rack.class))).thenReturn(updatedRack);

    final var result = this.updateRackImpl.execute(updateRequest);

    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(MAINTENANCE);

    verify(this.rackRepository).findById(rackId);
    verify(this.rackRepository).update(any(Rack.class));
  }

  @Test
  void execute_whenRackNotFound_shouldThrowRackNotFoundException() {
    final var rackId = UUID.randomUUID();

    final var updateRequest = UpdateRackRequest
        .builder()
        .id(rackId)
        .status(MAINTENANCE)
        .build();

    when(this.rackRepository.findById(rackId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> this.updateRackImpl.execute(updateRequest))
        .isInstanceOf(RackNotFoundException.class)
        .hasMessage("Rack with id '%s' not found".formatted(rackId));

    verify(this.rackRepository).findById(rackId);
  }

  @Test
  void execute_whenPartialUpdate_shouldPreserveExistingValues() {
    final var rackId = UUID.randomUUID();

    final var existingRack = Rack
        .builder()
        .id(rackId)
        .serial(1)
        .status(ACTIVE)
        .createdAt(Instant.now())
        .build();

    final var updateRequest = UpdateRackRequest
        .builder()
        .id(rackId)
        .status(null)
        .build();

    final var updatedRack = Rack
        .builder()
        .id(rackId)
        .serial(1)
        .status(ACTIVE)
        .createdAt(Instant.now())
        .build();

    when(this.rackRepository.findById(rackId)).thenReturn(Optional.of(existingRack));
    when(this.rackRepository.update(any(Rack.class))).thenReturn(updatedRack);

    final var result = this.updateRackImpl.execute(updateRequest);

    assertThat(result.status()).isEqualTo(ACTIVE);

    verify(this.rackRepository).update(any(Rack.class));
  }
}
