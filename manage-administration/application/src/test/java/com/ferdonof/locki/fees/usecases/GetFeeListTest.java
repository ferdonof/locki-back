package com.ferdonof.locki.fees.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ferdonof.locki.fees.entities.Fee;
import com.ferdonof.locki.fees.ports.FeeRepositoryPort;
import com.ferdonof.locki.lockers.enums.LockerSize;

@ExtendWith(MockitoExtension.class)
class GetFeeListTest {

  @Mock
  private FeeRepositoryPort feeRepositoryPort;

  @InjectMocks
  private GetFeeListImpl getFeeListImpl;

  @Test
  void execute_whenFeesExistWithAllCriteria_shouldReturnList() {
    final var feeId = UUID.randomUUID();
    final var country = "ARGENTINA";
    final var currency = "ARS";

    final var fee = Fee
        .builder()
        .id(feeId)
        .lockerSize(LockerSize.SMALL)
        .country(country)
        .currency(currency)
        .price(new BigDecimal("10.50"))
        .version(1L)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();

    when(this.feeRepositoryPort.findFees(any(Fee.class))).thenReturn(List.of(fee));

    final var result = this.getFeeListImpl.execute(feeId, country, currency);

    assertThat(result)
        .isNotNull()
        .hasSize(1);
    assertThat(result.get(0)).isEqualTo(fee);
    assertThat(result
        .get(0)
        .id()).isEqualTo(feeId);
    assertThat(result
        .get(0)
        .country()).isEqualTo(country);
    assertThat(result
        .get(0)
        .currency()).isEqualTo(currency);

    verify(this.feeRepositoryPort).findFees(any(Fee.class));
  }

  @Test
  void execute_whenNoFeesExist_shouldReturnEmptyList() {
    final var feeId = UUID.randomUUID();
    final var country = "SPAIN";
    final var currency = "EUR";

    when(this.feeRepositoryPort.findFees(any(Fee.class))).thenReturn(List.of());

    final var result = this.getFeeListImpl.execute(feeId, country, currency);

    assertThat(result)
        .isNotNull()
        .isEmpty();

    verify(this.feeRepositoryPort).findFees(any(Fee.class));
  }

  @Test
  void execute_withMultipleFees_shouldReturnAll() {
    final var country = "MEXICO";
    final var currency = "MXN";

    final var fee1 = Fee
        .builder()
        .id(UUID.randomUUID())
        .lockerSize(LockerSize.SMALL)
        .country(country)
        .currency(currency)
        .price(new BigDecimal("5.00"))
        .build();

    final var fee2 = Fee
        .builder()
        .id(UUID.randomUUID())
        .lockerSize(LockerSize.MEDIUM)
        .country(country)
        .currency(currency)
        .price(new BigDecimal("10.00"))
        .build();

    final var fee3 = Fee
        .builder()
        .id(UUID.randomUUID())
        .lockerSize(LockerSize.LARGE)
        .country(country)
        .currency(currency)
        .price(new BigDecimal("15.00"))
        .build();

    final var fees = List.of(fee1, fee2, fee3);

    when(this.feeRepositoryPort.findFees(any(Fee.class))).thenReturn(fees);

    final var result = this.getFeeListImpl.execute(null, country, currency);

    assertThat(result).hasSize(3);
    assertThat(result).containsExactlyInAnyOrder(fee1, fee2, fee3);

    verify(this.feeRepositoryPort).findFees(any(Fee.class));
  }

  @Test
  void execute_withNullId_shouldReturnFeesByCountryAndCurrency() {
    final var country = "USA";
    final var currency = "USD";

    final var fee = Fee
        .builder()
        .id(UUID.randomUUID())
        .lockerSize(LockerSize.MEDIUM)
        .country(country)
        .currency(currency)
        .price(new BigDecimal("20.00"))
        .build();

    when(this.feeRepositoryPort.findFees(any(Fee.class))).thenReturn(List.of(fee));

    final var result = this.getFeeListImpl.execute(null, country, currency);

    assertThat(result).hasSize(1);
    assertThat(result
        .get(0)
        .country()).isEqualTo(country);
    assertThat(result
        .get(0)
        .currency()).isEqualTo(currency);

    verify(this.feeRepositoryPort).findFees(any(Fee.class));
  }

  @Test
  void execute_withNullCountry_shouldReturnFeesByIdAndCurrency() {
    final var feeId = UUID.randomUUID();
    final var currency = "GBP";

    final var fee = Fee
        .builder()
        .id(feeId)
        .lockerSize(LockerSize.LARGE)
        .country("UNITED_KINGDOM")
        .currency(currency)
        .price(new BigDecimal("25.00"))
        .build();

    when(this.feeRepositoryPort.findFees(any(Fee.class))).thenReturn(List.of(fee));

    final var result = this.getFeeListImpl.execute(feeId, null, currency);

    assertThat(result).hasSize(1);
    assertThat(result
        .get(0)
        .id()).isEqualTo(feeId);
    assertThat(result
        .get(0)
        .currency()).isEqualTo(currency);

    verify(this.feeRepositoryPort).findFees(any(Fee.class));
  }

  @Test
  void execute_withNullCurrency_shouldReturnFeesByIdAndCountry() {
    final var feeId = UUID.randomUUID();
    final var country = "FRANCE";

    final var fee = Fee
        .builder()
        .id(feeId)
        .lockerSize(LockerSize.SMALL)
        .country(country)
        .currency("EUR")
        .price(new BigDecimal("12.50"))
        .build();

    when(this.feeRepositoryPort.findFees(any(Fee.class))).thenReturn(List.of(fee));

    final var result = this.getFeeListImpl.execute(feeId, country, null);

    assertThat(result).hasSize(1);
    assertThat(result
        .get(0)
        .id()).isEqualTo(feeId);
    assertThat(result
        .get(0)
        .country()).isEqualTo(country);

    verify(this.feeRepositoryPort).findFees(any(Fee.class));
  }

  @Test
  void execute_withAllNullParameters_shouldReturnAllFees() {
    final var fee1 = Fee
        .builder()
        .id(UUID.randomUUID())
        .lockerSize(LockerSize.SMALL)
        .country("ARGENTINA")
        .currency("ARS")
        .price(new BigDecimal("10.00"))
        .build();

    final var fee2 = Fee
        .builder()
        .id(UUID.randomUUID())
        .lockerSize(LockerSize.MEDIUM)
        .country("SPAIN")
        .currency("EUR")
        .price(new BigDecimal("20.00"))
        .build();

    final var fees = List.of(fee1, fee2);

    when(this.feeRepositoryPort.findFees(any(Fee.class))).thenReturn(fees);

    final var result = this.getFeeListImpl.execute(null, null, null);

    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyInAnyOrder(fee1, fee2);

    verify(this.feeRepositoryPort).findFees(any(Fee.class));
  }

  @Test
  void execute_withDifferentCurrencies_shouldFilterCorrectly() {
    final var country = "GLOBAL";

    final var feeARS = Fee
        .builder()
        .id(UUID.randomUUID())
        .lockerSize(LockerSize.SMALL)
        .country(country)
        .currency("ARS")
        .price(new BigDecimal("10.00"))
        .build();

    final var feeEUR = Fee
        .builder()
        .id(UUID.randomUUID())
        .lockerSize(LockerSize.SMALL)
        .country(country)
        .currency("EUR")
        .price(new BigDecimal("20.00"))
        .build();

    when(this.feeRepositoryPort.findFees(any(Fee.class))).thenReturn(List.of(feeARS));

    final var result = this.getFeeListImpl.execute(null, country, "ARS");

    assertThat(result).hasSize(1);
    assertThat(result
        .get(0)
        .currency()).isEqualTo("ARS");

    verify(this.feeRepositoryPort).findFees(any(Fee.class));
  }

  @Test
  void execute_shouldPreserveAllFeeProperties() {
    final var feeId = UUID.randomUUID();
    final var now = Instant.now();

    final var fee = Fee
        .builder()
        .id(feeId)
        .lockerSize(LockerSize.MEDIUM)
        .country("ITALY")
        .currency("EUR")
        .price(new BigDecimal("18.75"))
        .version(3L)
        .createdAt(now.minusSeconds(86400))
        .updatedAt(now)
        .build();

    when(this.feeRepositoryPort.findFees(any(Fee.class))).thenReturn(List.of(fee));

    final var result = this.getFeeListImpl.execute(feeId, "ITALY", "EUR");

    assertThat(result).hasSize(1);
    final var retrievedFee = result.get(0);

    assertThat(retrievedFee.id()).isEqualTo(feeId);
    assertThat(retrievedFee.lockerSize()).isEqualTo(LockerSize.MEDIUM);
    assertThat(retrievedFee.country()).isEqualTo("ITALY");
    assertThat(retrievedFee.currency()).isEqualTo("EUR");
    assertThat(retrievedFee.price()).isEqualTo(new BigDecimal("18.75"));
    assertThat(retrievedFee.version()).isEqualTo(3L);
    assertThat(retrievedFee.createdAt()).isEqualTo(now.minusSeconds(86400));
    assertThat(retrievedFee.updatedAt()).isEqualTo(now);

    verify(this.feeRepositoryPort).findFees(any(Fee.class));
  }
}

