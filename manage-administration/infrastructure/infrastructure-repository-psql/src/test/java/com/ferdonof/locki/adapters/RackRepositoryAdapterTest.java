package com.ferdonof.locki.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ferdonof.locki.entities.RackEntity;
import com.ferdonof.locki.mappers.RackMapper;
import com.ferdonof.locki.racks.entity.Rack;
import com.ferdonof.locki.repositories.RackRepository;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class RackRepositoryAdapterTest {

  @Mock
  private RackMapper rackMapper;

  @Mock
  private RackRepository rackRepository;

  @InjectMocks
  private RackRepositoryAdapter rackRepositoryAdapter;

  @Test
  void findById_whenRackExists_shouldReturnRack() {
    final UUID rackId = UUID.randomUUID();
    final RackEntity entity = RackEntity
        .builder()
        .id(rackId)
        .serial(1)
        .version(0L)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
    final Rack expectedRack = Rack
        .builder()
        .id(rackId)
        .serial(1)
        .version(0L)
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .build();

    when(this.rackRepository.findById(rackId)).thenReturn(Optional.of(entity));
    when(this.rackMapper.toDomain(entity)).thenReturn(expectedRack);

    final Optional<Rack> result = this.rackRepositoryAdapter.findById(rackId);

    assertThat(result)
        .isPresent()
        .contains(expectedRack);
    assertThat(result
        .get()
        .id()).isEqualTo(rackId);
    verify(this.rackRepository).findById(rackId);
    verify(this.rackMapper).toDomain(entity);
  }

  @Test
  void findById_whenRackDoesNotExist_shouldReturnEmpty() {
    final UUID rackId = UUID.randomUUID();

    when(this.rackRepository.findById(rackId)).thenReturn(Optional.empty());

    final Optional<Rack> result = this.rackRepositoryAdapter.findById(rackId);

    assertThat(result).isEmpty();
    verify(this.rackRepository).findById(rackId);
  }
}
