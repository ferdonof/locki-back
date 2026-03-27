package com.ferdonof.locki.locations.usecases;

import com.ferdonof.locki.locations.entities.Location;
import com.ferdonof.locki.locations.ports.LocationRepositoryPort;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CreateLocationTest {

	@Mock
	private LocationRepositoryPort locationRepositoryPort;

	@InjectMocks
	private CreateLocationImpl createLocation;

	@Test
	void execute_whenValidLocation_shouldReturnSavedLocation() {
		final Location input = Location.builder()
				.code("MAD")
				.city("Madrid")
				.country("Spain")
				.latitude(new BigDecimal("40.416775"))
				.longitude(new BigDecimal("-3.703790"))
				.build();
		final Location expected = Location.builder()
				.id(UUID.randomUUID())
				.code("MAD")
				.city("Madrid")
				.country("Spain")
				.latitude(new BigDecimal("40.416775"))
				.longitude(new BigDecimal("-3.703790"))
				.version(0L)
				.createdAt(Instant.now())
				.updatedAt(Instant.now())
				.build();

		when(this.locationRepositoryPort.insert(input)).thenReturn(expected);

		final Location result = this.createLocation.execute(input);

		assertThat(result).isEqualTo(expected);
		assertThat(result.id()).isNotNull();
		assertThat(result.code()).isEqualTo("MAD");
		verify(this.locationRepositoryPort).insert(input);
	}
}

