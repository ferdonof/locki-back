package com.ferdonof.locki.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.ferdonof.locki.entities.LocationEntity;
import com.ferdonof.locki.locations.entities.Location;

@Tag("unit")
class LocationMapperTest {

	private final LocationMapper locationMapper = Mappers.getMapper(LocationMapper.class);

	@Test
	void toDomain_whenValidEntity_shouldMapAllFields() {
		final UUID id = UUID.randomUUID();
		final Instant now = Instant.now();
		final LocationEntity entity = LocationEntity.builder()
				.id(id)
				.code("MAD")
				.address("123 Main St")
				.city("Madrid")
				.country("Spain")
				.latitude(new BigDecimal("40.416775"))
				.longitude(new BigDecimal("-3.703790"))
				.version(1L)
				.createdAt(now)
				.updatedAt(now)
				.build();

		final Location result = this.locationMapper.toDomain(entity);

		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(id);
		assertThat(result.code()).isEqualTo("MAD");
		assertThat(result.address()).isEqualTo("123 Main St");
		assertThat(result.city()).isEqualTo("Madrid");
		assertThat(result.country()).isEqualTo("Spain");
		assertThat(result.latitude()).isEqualByComparingTo(new BigDecimal("40.416775"));
		assertThat(result.longitude()).isEqualByComparingTo(new BigDecimal("-3.703790"));
		assertThat(result.version()).isEqualTo(1L);
		assertThat(result.createdAt()).isEqualTo(now);
		assertThat(result.updatedAt()).isEqualTo(now);
	}

	@Test
	void toDomain_whenNullEntity_shouldReturnNull() {
		assertThat(this.locationMapper.toDomain(null)).isNull();
	}

	@Test
	void toEntity_whenValidDomain_shouldMapAllFields() {
		final UUID id = UUID.randomUUID();
		final Instant now = Instant.now();
		final Location location = Location.builder()
				.id(id)
				.code("BCN")
				.address("456 Oak Ave")
				.city("Barcelona")
				.country("Spain")
				.latitude(new BigDecimal("41.385064"))
				.longitude(new BigDecimal("2.173404"))
				.version(0L)
				.createdAt(now)
				.updatedAt(now)
				.build();

		final LocationEntity result = this.locationMapper.toEntity(location);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(id);
		assertThat(result.getCode()).isEqualTo("BCN");
		assertThat(result.getAddress()).isEqualTo("456 Oak Ave");
		assertThat(result.getCity()).isEqualTo("Barcelona");
		assertThat(result.getCountry()).isEqualTo("Spain");
		assertThat(result.getLatitude()).isEqualByComparingTo(new BigDecimal("41.385064"));
		assertThat(result.getLongitude()).isEqualByComparingTo(new BigDecimal("2.173404"));
		assertThat(result.getVersion()).isEqualTo(0L);
		assertThat(result.getCreatedAt()).isEqualTo(now);
		assertThat(result.getUpdatedAt()).isEqualTo(now);
	}

	@Test
	void toEntity_whenNullDomain_shouldReturnNull() {
		assertThat(this.locationMapper.toEntity(null)).isNull();
	}
}