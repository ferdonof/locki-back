package com.ferdonof.locki.racks.usecases;

import com.ferdonof.locki.racks.entity.Rack;
import com.ferdonof.locki.racks.exceptions.RackNotFoundException;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.ferdonof.locki.racks.enums.RackStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetRackTest {

	@Mock
	private RackRepositoryPort rackRepository;

	@InjectMocks
	private GetRackImpl getRackImpl;

	@Test
	void execute_whenRackExists_shouldReturnRack() {
		final var rackId = UUID.randomUUID();
		final var rack = Rack.builder().id(rackId).number(1).status(ACTIVE).createdAt(Instant.now()).build();

		when(this.rackRepository.findById(rackId)).thenReturn(Optional.of(rack));

		final var result = this.getRackImpl.execute(rackId);

		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(rackId);
		assertThat(result.number()).isEqualTo(1);
		assertThat(result.status()).isEqualTo(ACTIVE);

		verify(this.rackRepository).findById(rackId);
	}

	@Test
	void execute_whenRackNotExists_shouldThrowRackNotFoundException() {
		final var rackId = UUID.randomUUID();

		when(this.rackRepository.findById(rackId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> this.getRackImpl.execute(rackId)).isInstanceOf(RackNotFoundException.class)
				.hasMessage("Rack with id '%s' not found".formatted(rackId));

		verify(this.rackRepository).findById(rackId);
	}
}
