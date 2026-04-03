package com.ferdonof.locki.racks.usecases;

import com.ferdonof.locki.locations.entities.Location;
import com.ferdonof.locki.locations.ports.LocationRepositoryPort;
import com.ferdonof.locki.racks.entity.CreateRackRequest;
import com.ferdonof.locki.racks.entity.Rack;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.ferdonof.locki.racks.enums.RackStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateRackImplTest {

	@Mock
	private TransactionTemplate txTemplate;

	@Mock
	private RackRepositoryPort rackRepositoryPort;

	@Mock
	private LocationRepositoryPort locationRepositoryPort;

	@InjectMocks
	private CreateRackImpl createRackImpl;

	@BeforeEach
	void setup() {
		when(this.txTemplate.execute(any())).thenAnswer(invocation -> invocation.<TransactionCallback<?>>getArgument(0)
				.doInTransaction(mock(TransactionStatus.class)));
	}

	@Test
	void execute_whenValidRackWithLocation_shouldCreateRack() {
		final var locationId = UUID.randomUUID();
		final var rackId = UUID.randomUUID();

		final var location = Location.builder().id(locationId).code("MAD").city("Madrid").country("Spain")
				.latitude(BigDecimal.valueOf(40.4168)).longitude(BigDecimal.valueOf(-3.7038)).build();

		final var request = CreateRackRequest.builder().number(1).status(ACTIVE).locationId(locationId).build();

		final var createdRack = Rack.builder().id(rackId).number(1).status(ACTIVE).location(location)
				.createdAt(Instant.now()).build();

		when(this.locationRepositoryPort.findById(locationId)).thenReturn(Optional.of(location));

		when(this.rackRepositoryPort.insert(any(Rack.class))).thenReturn(createdRack);

		final var result = this.createRackImpl.execute(request);

		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(rackId);
		assertThat(result.number()).isEqualTo(1);
		assertThat(result.status()).isEqualTo(ACTIVE);
		assertThat(result.location()).isNotNull();

		verify(this.locationRepositoryPort).findById(locationId);
		verify(this.rackRepositoryPort).insert(any(Rack.class));
	}

	@Test
	void execute_whenRackWithoutLocation_shouldCreateRackWithNullLocation() {
		final var rackId = UUID.randomUUID();

		final var request = CreateRackRequest.builder().number(1).status(ACTIVE).locationId(null).build();

		final var createdRack = Rack.builder().id(rackId).number(1).status(ACTIVE).location(null)
				.createdAt(Instant.now()).build();

		when(this.rackRepositoryPort.insert(any(Rack.class))).thenReturn(createdRack);

		final var result = this.createRackImpl.execute(request);

		assertThat(result).isNotNull();
		assertThat(result.location()).isNull();
		assertThat(result.number()).isEqualTo(1);

		verify(this.rackRepositoryPort).insert(any(Rack.class));
	}
}
