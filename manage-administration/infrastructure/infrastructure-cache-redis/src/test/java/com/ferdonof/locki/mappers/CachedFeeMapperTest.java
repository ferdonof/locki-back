package com.ferdonof.locki.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.ferdonof.locki.entities.CachedFee;
import com.ferdonof.locki.fees.entities.Fee;
import com.ferdonof.locki.lockers.enums.LockerSize;

@Tag("unit")
class CachedFeeMapperTest {

  private final CachedFeeMapper cachedFeeMapper = Mappers.getMapper(CachedFeeMapper.class);

  @Test
  void toCachedFee_whenValidDomain_shouldMapCommonFields() {
    final UUID id = UUID.randomUUID();
    final Fee fee = Fee
        .builder()
        .id(id)
        .lockerSize(LockerSize.LARGE)
        .country("SPAIN")
        .currency("EUR")
        .price(new BigDecimal("12.50"))
        .build();

    final CachedFee result = this.cachedFeeMapper.toCachedFee(fee);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(id);
    assertThat(result.lockerSize()).isEqualTo("LARGE");
    assertThat(result.country()).isEqualTo("SPAIN");
    assertThat(result.currency()).isEqualTo("EUR");
    assertThat(result.price()).isEqualByComparingTo(new BigDecimal("12.50"));
  }

  @Test
  void toCachedFee_whenNullDomain_shouldReturnNull() {
    assertThat(this.cachedFeeMapper.toCachedFee(null)).isNull();
  }

  @Test
  void toDomain_whenValidCached_shouldMapCommonFields() {
    final UUID id = UUID.randomUUID();
    final CachedFee cached = CachedFee
        .builder()
        .id(id)
        .lockerSize("MEDIUM")
        .country("ARGENTINA")
        .currency("ARS")
        .price(new BigDecimal("100.00"))
        .build();

    final Fee result = this.cachedFeeMapper.toDomain(cached);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(id);
    assertThat(result.lockerSize()).isEqualTo(LockerSize.MEDIUM);
    assertThat(result.country()).isEqualTo("ARGENTINA");
    assertThat(result.currency()).isEqualTo("ARS");
    assertThat(result.price()).isEqualTo("100.00");
    assertThat(result.version()).isNull();
    assertThat(result.createdAt()).isNull();
    assertThat(result.updatedAt()).isNull();
  }

  @Test
  void toDomain_whenNullCached_shouldReturnNull() {
    assertThat(this.cachedFeeMapper.toDomain(null)).isNull();
  }
}