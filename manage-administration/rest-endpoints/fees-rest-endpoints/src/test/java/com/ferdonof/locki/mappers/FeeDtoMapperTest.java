package com.ferdonof.locki.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.ferdonof.locki.external.admin.dto.CreateFeeRequestDTO;
import com.ferdonof.locki.external.admin.dto.SizeDTO;
import com.ferdonof.locki.fees.entities.Fee;
import com.ferdonof.locki.lockers.enums.LockerSize;

@Tag("unit")
class FeeDtoMapperTest {

  private final FeeDtoMapper feeDtoMapper = Mappers.getMapper(FeeDtoMapper.class);

  @Test
  void toDomain_whenValidRequest_shouldMapAllFields() {
    final CreateFeeRequestDTO dto = new CreateFeeRequestDTO();
    dto.setLockerSize(SizeDTO.SMALL);
    dto.setCountry("ARGENTINA");
    dto.setCurrency("ARS");
    dto.setPrice(23.50f);

    final var result = this.feeDtoMapper.toDomain(dto);

    assertThat(result).isNotNull();
    assertThat(result.lockerSize()).isEqualTo(LockerSize.SMALL);
    assertThat(result.country()).isEqualTo("ARGENTINA");
    assertThat(result.currency()).isEqualTo("ARS");
    assertThat(result.price()).isEqualTo("23.5");
  }

  @Test
  void toDomain_whenNullRequest_shouldReturnNull() {
    assertThat(this.feeDtoMapper.toDomain(null)).isNull();
  }

  @Test
  void toDomain_whenNullLockerSize_shouldMapNullLockerSize() {
    final CreateFeeRequestDTO dto = new CreateFeeRequestDTO();
    dto.setLockerSize(null);
    dto.setCountry("SPAIN");
    dto.setCurrency("EUR");

    final var result = this.feeDtoMapper.toDomain(dto);

    assertThat(result.lockerSize()).isNull();
    assertThat(result.country()).isEqualTo("SPAIN");
  }

  @Test
  void toDomain_withAllLockerSizes_shouldMapCorrectly() {
    for (final SizeDTO size : SizeDTO.values()) {
      final CreateFeeRequestDTO dto = new CreateFeeRequestDTO();
      dto.setLockerSize(size);

      final var result = this.feeDtoMapper.toDomain(dto);

      assertThat(result.lockerSize()).isEqualTo(LockerSize.valueOf(size.getValue()));
    }
  }

  @Test
  void toDto_whenValidFee_shouldMapAllFields() {
    final UUID id = UUID.randomUUID();
    final Instant now = Instant.now();

    final var fee = Fee
        .builder()
        .id(id)
        .lockerSize(LockerSize.LARGE)
        .country("MEXICO")
        .currency("MXN")
        .price("99.99")
        .version(2L)
        .createdAt(now)
        .updatedAt(now)
        .build();

    final var result = this.feeDtoMapper.toDto(fee);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(id);
    assertThat(result.getLockerSize()).isEqualTo(SizeDTO.LARGE);
    assertThat(result.getCountry()).isEqualTo("MEXICO");
    assertThat(result.getCurrency()).isEqualTo("MXN");
    assertThat(result.getPrice()).isEqualTo(99.99f);
  }

  @Test
  void toDto_whenNullFee_shouldReturnNull() {
    assertThat(this.feeDtoMapper.toDto(null)).isNull();
  }

  @Test
  void toDto_whenNullLockerSize_shouldMapNullLockerSize() {
    final var fee = Fee
        .builder()
        .id(UUID.randomUUID())
        .lockerSize(null)
        .country("CHILE")
        .currency("CLP")
        .build();

    final var result = this.feeDtoMapper.toDto(fee);

    assertThat(result.getLockerSize()).isNull();
    assertThat(result.getCountry()).isEqualTo("CHILE");
  }

  @Test
  void toDto_withAllLockerSizes_shouldMapCorrectly() {
    for (final LockerSize size : LockerSize.values()) {
      final var fee = Fee
          .builder()
          .lockerSize(size)
          .build();

      final var result = this.feeDtoMapper.toDto(fee);

      assertThat(result.getLockerSize()).isEqualTo(SizeDTO.fromValue(size.name()));
    }
  }

  @Test
  void toUpdateRequest_whenValidRequest_shouldMapIdAndAllFields() {
    final UUID id = UUID.randomUUID();
    final CreateFeeRequestDTO dto = new CreateFeeRequestDTO();
    dto.setLockerSize(SizeDTO.MEDIUM);
    dto.setCountry("COLOMBIA");
    dto.setCurrency("COP");
    dto.setPrice(15.00f);

    final var result = this.feeDtoMapper.toUpdateRequest(id, dto);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(id);
    assertThat(result.lockerSize()).isEqualTo(LockerSize.MEDIUM);
    assertThat(result.country()).isEqualTo("COLOMBIA");
    assertThat(result.currency()).isEqualTo("COP");
    assertThat(result.price()).isEqualTo("15.0");
  }

  @Test
  void toUpdateRequest_whenNullLockerSize_shouldMapNullLockerSize() {
    final UUID id = UUID.randomUUID();
    final CreateFeeRequestDTO dto = new CreateFeeRequestDTO();
    dto.setLockerSize(null);
    dto.setCountry("PERU");
    dto.setCurrency("PEN");

    final var result = this.feeDtoMapper.toUpdateRequest(id, dto);

    assertThat(result.id()).isEqualTo(id);
    assertThat(result.lockerSize()).isNull();
    assertThat(result.country()).isEqualTo("PERU");
  }

  @Test
  void toUpdateRequest_whenNullFields_shouldPreserveId() {
    final UUID id = UUID.randomUUID();
    final CreateFeeRequestDTO dto = new CreateFeeRequestDTO();

    final var result = this.feeDtoMapper.toUpdateRequest(id, dto);

    assertThat(result.id()).isEqualTo(id);
    assertThat(result.lockerSize()).isNull();
    assertThat(result.country()).isNull();
    assertThat(result.currency()).isNull();
    assertThat(result.price()).isNull();
  }
}

