package com.ferdonof.locki.locations.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ferdonof.locki.locations.entities.Location;
import com.ferdonof.locki.locations.ports.LocationRepositoryPort;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CreateLocationTest {
  @Mock
  private TransactionTemplate txTemplate;

  @Mock
  private LocationRepositoryPort locationRepositoryPort;

  @InjectMocks
  private CreateLocationImpl createLocation;

  @BeforeEach
  void setup() {
    when(this.txTemplate.execute(any())).thenAnswer(invocation -> invocation
        .<TransactionCallback<?>>getArgument(0)
        .doInTransaction(mock(TransactionStatus.class)));
  }

  @Test
  void execute_whenValidLocation_shouldReturnSavedLocation() {
    final Location input = Location
        .builder()
        .code("MAD")
        .city("Madrid")
        .country("Spain")
        .lat(new BigDecimal("40.416775"))
        .lon(new BigDecimal("-3.703790"))
        .build();
    final Location expected = Location
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

    when(this.locationRepositoryPort.insert(input)).thenReturn(expected);

    final Location result = this.createLocation.execute(input);

    assertThat(result).isEqualTo(expected);
    assertThat(result.id()).isNotNull();
    assertThat(result.code()).isEqualTo("MAD");
    verify(this.locationRepositoryPort).insert(input);
  }
}
