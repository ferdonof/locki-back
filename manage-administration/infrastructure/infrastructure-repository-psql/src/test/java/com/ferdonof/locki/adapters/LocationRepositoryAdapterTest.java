package com.ferdonof.locki.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.ferdonof.locki.commons.exceptions.GenericClientException;
import com.ferdonof.locki.entities.LocationEntity;
import com.ferdonof.locki.locations.entities.Location;
import com.ferdonof.locki.mappers.LocationMapper;
import com.ferdonof.locki.repositories.LocationRepository;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class LocationRepositoryAdapterTest {
  @Mock
  private LocationMapper locationMapper;

  @Mock
  private LocationRepository locationRepository;

  @InjectMocks
  private LocationRepositoryAdapter locationRepositoryAdapter;

  @Test
  void insert_whenValidLocation_shouldReturnSavedLocation() {
    final Location location = Location
        .builder()
        .code("MAD")
        .city("Madrid")
        .country("Spain")
        .lat(new BigDecimal("40.416775"))
        .lon(new BigDecimal("-3.703790"))
        .build();
    final LocationEntity entity = LocationEntity
        .builder()
        .id(UUID.randomUUID())
        .code("MAD")
        .city("Madrid")
        .country("Spain")
        .lat(new BigDecimal("40.416775"))
        .lon(new BigDecimal("-3.703790"))
        .version(0L)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
    final Location expectedLocation = Location
        .builder()
        .id(entity.getId())
        .code("MAD")
        .city("Madrid")
        .country("Spain")
        .lat(new BigDecimal("40.416775"))
        .lon(new BigDecimal("-3.703790"))
        .version(0L)
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .build();

    when(this.locationMapper.toEntity(location)).thenReturn(entity);
    when(this.locationRepository.saveAndFlush(entity)).thenReturn(entity);
    when(this.locationMapper.toDomain(entity)).thenReturn(expectedLocation);

    final Location result = this.locationRepositoryAdapter.insert(location);

    assertThat(result).isEqualTo(expectedLocation);
    assertThat(result.id()).isNotNull();
    assertThat(result.code()).isEqualTo("MAD");
    assertThat(result.city()).isEqualTo("Madrid");
    verify(this.locationMapper).toEntity(location);
    verify(this.locationRepository).saveAndFlush(entity);
    verify(this.locationMapper).toDomain(entity);
  }

  @Test
  void insert_whenDataIntegrityViolation_shouldThrowGenericClientException() {
    final Location location = Location
        .builder()
        .code("MAD")
        .city("Madrid")
        .country("Spain")
        .build();
    final LocationEntity entity = LocationEntity
        .builder()
        .code("MAD")
        .build();

    when(this.locationMapper.toEntity(location)).thenReturn(entity);
    when(this.locationRepository.saveAndFlush(entity)).thenThrow(
        new DataIntegrityViolationException("constraint violation",
            new ConstraintViolationException("constraint", new SQLException(), "some_constraint")));

    assertThatThrownBy(() -> this.locationRepositoryAdapter.insert(location))
        .isInstanceOf(GenericClientException.class);
  }
}

