package com.ferdonof.locki.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.ferdonof.locki.external.admin.dto.CreateLocationRequestDTO;
import com.ferdonof.locki.external.admin.dto.LocationResponseDTO;
import com.ferdonof.locki.locations.entities.Location;

@Tag("unit")
class LocationDtoMapperTest {

	private final LocationDtoMapper locationDtoMapper = Mappers.getMapper(LocationDtoMapper.class);

	@Test
	void toDomain_whenValidRequest_shouldMapAllFields() {
		final CreateLocationRequestDTO request = new CreateLocationRequestDTO("Madrid", "Spain", "MAD");
		request.setLatitude(40.4168f);
		request.setLongitude(-3.7038f);

		final Location result = this.locationDtoMapper.toDomain(request);

		assertThat(result).isNotNull();
		assertThat(result.code()).isEqualTo("MAD");
		assertThat(result.city()).isEqualTo("Madrid");
		assertThat(result.country()).isEqualTo("Spain");
		assertThat(result.latitude().floatValue()).isEqualTo(40.4168f);
		assertThat(result.longitude().floatValue()).isEqualTo(-3.7038f);
	}

	@Test
	void toDomain_whenNullRequest_shouldReturnNull() {
		assertThat(this.locationDtoMapper.toDomain(null)).isNull();
	}

	@Test
	void toDto_whenValidLocation_shouldMapAllFields() {
		final UUID id = UUID.randomUUID();
		final Location location = Location.builder()
				.id(id)
				.code("MAD")
				.city("Madrid")
				.country("Spain")
				.latitude(new BigDecimal("40.416775"))
				.longitude(new BigDecimal("-3.703790"))
				.version(0L)
				.createdAt(Instant.now())
				.updatedAt(Instant.now())
				.build();

		final LocationResponseDTO result = this.locationDtoMapper.toDto(location);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getCity()).isEqualTo("Madrid");
		assertThat(result.getCountry()).isEqualTo("Spain");
		assertThat(result.getLatitude()).isNotNull();
		assertThat(result.getLongitude()).isNotNull();
	}

	@Test
	void toDto_whenNullLocation_shouldReturnNull() {
		assertThat(this.locationDtoMapper.toDto(null)).isNull();
	}
}

