package com.ferdonof.locki.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.ferdonof.locki.entities.FeeEntity;
import com.ferdonof.locki.fees.entities.Fee;
import com.ferdonof.locki.lockers.enums.LockerSize;

@Tag("unit")
class FeeMapperTest {

  private final FeeMapper feeMapper = Mappers.getMapper(FeeMapper.class);

  @Test
  void toDomain_whenValidEntity_shouldMapAllFields() {
    final UUID id = UUID.randomUUID();
    final Instant now = Instant.now();
    final FeeEntity entity = FeeEntity
        .builder()
        .id(id)
        .lockerSize(LockerSize.SMALL)
        .country("ARGENTINA")
        .currency("ARS")
        .price(new BigDecimal("23.50"))
        .version(1L)
        .createdAt(now)
        .updatedAt(now)
        .build();

    final Fee result = this.feeMapper.toDomain(entity);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(id);
    assertThat(result.lockerSize()).isEqualTo(LockerSize.SMALL);
    assertThat(result.country()).isEqualTo("ARGENTINA");
    assertThat(result.currency()).isEqualTo("ARS");
    assertThat(result.price()).isEqualTo("23.50");
    assertThat(result.version()).isEqualTo(1L);
    assertThat(result.createdAt()).isEqualTo(now);
    assertThat(result.updatedAt()).isEqualTo(now);
  }

  @Test
  void toDomain_whenNullEntity_shouldReturnNull() {
    assertThat(this.feeMapper.toDomain(null)).isNull();
  }

  @Test
  void toEntity_whenValidDomain_shouldMapAllFields() {
    final UUID id = UUID.randomUUID();
    final Instant now = Instant.now();
    final Fee fee = Fee
        .builder()
        .id(id)
        .lockerSize(LockerSize.MEDIUM)
        .country("SPAIN")
        .currency("EUR")
        .price(new BigDecimal("9.99"))
        .version(2L)
        .createdAt(now)
        .updatedAt(now)
        .build();

    final FeeEntity result = this.feeMapper.toEntity(fee);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(id);
    assertThat(result.getLockerSize()).isEqualTo(LockerSize.MEDIUM);
    assertThat(result.getCountry()).isEqualTo("SPAIN");
    assertThat(result.getCurrency()).isEqualTo("EUR");
    assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("9.99"));
    assertThat(result.getVersion()).isEqualTo(2L);
    assertThat(result.getCreatedAt()).isEqualTo(now);
    assertThat(result.getUpdatedAt()).isEqualTo(now);
  }

  @Test
  void toEntity_whenNullDomain_shouldReturnNull() {
    assertThat(this.feeMapper.toEntity(null)).isNull();
  }
}

