package com.ferdonof.locki.fees.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ferdonof.locki.fees.entities.Fee;
import com.ferdonof.locki.fees.ports.FeeCachePort;
import com.ferdonof.locki.fees.ports.FeeRepositoryPort;
import com.ferdonof.locki.lockers.enums.LockerSize;

@ExtendWith(MockitoExtension.class)
class CreateFeeTest {

  @Mock
  private TransactionTemplate txTemplate;

  @Mock
  private FeeRepositoryPort feeRepositoryPort;

  @Mock
  private FeeCachePort feeCachePort;

  @InjectMocks
  private CreateFeeImpl createFeeImpl;

  @BeforeEach
  void setup() {
    when(this.txTemplate.execute(any())).thenAnswer(invocation -> invocation
        .<TransactionCallback<?>>getArgument(0)
        .doInTransaction(mock(TransactionStatus.class)));
  }

  @Test
  void execute_whenValidFee_shouldCreateFee() {
    final var feeId = UUID.randomUUID();
    final var now = Instant.now();

    final var request = Fee
        .builder()
        .lockerSize(LockerSize.SMALL)
        .country("ARGENTINA")
        .currency("ARS")
        .price("10.50")
        .build();

    final var createdFee = Fee
        .builder()
        .id(feeId)
        .lockerSize(LockerSize.SMALL)
        .country("ARGENTINA")
        .currency("ARS")
        .price("10.50")
        .version(1L)
        .createdAt(now)
        .updatedAt(now)
        .build();

    when(this.feeRepositoryPort.insert(any(Fee.class))).thenReturn(createdFee);

    final var result = this.createFeeImpl.execute(request);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(feeId);
    assertThat(result.lockerSize()).isEqualTo(LockerSize.SMALL);
    assertThat(result.country()).isEqualTo("ARGENTINA");
    assertThat(result.currency()).isEqualTo("ARS");
    assertThat(result.price()).isEqualTo("10.50");
    assertThat(result.version()).isEqualTo(1L);
    assertThat(result.createdAt()).isEqualTo(now);
    assertThat(result.updatedAt()).isEqualTo(now);

    verify(this.feeRepositoryPort).insert(any(Fee.class));
    verify(this.feeCachePort).put(createdFee);
  }

  @Test
  void execute_withDifferentLockerSizes_shouldCreateFeeForEachSize() {
    final var sizes = new LockerSize[] {LockerSize.SMALL, LockerSize.MEDIUM, LockerSize.LARGE};

    for (final LockerSize size : sizes) {
      final var feeId = UUID.randomUUID();
      final var request = Fee
          .builder()
          .lockerSize(size)
          .country("SPAIN")
          .currency("EUR")
          .price("15.00")
          .build();

      final var createdFee = Fee
          .builder()
          .id(feeId)
          .lockerSize(size)
          .country("SPAIN")
          .currency("EUR")
          .price("15.00")
          .version(1L)
          .createdAt(Instant.now())
          .updatedAt(Instant.now())
          .build();

      when(this.feeRepositoryPort.insert(any(Fee.class))).thenReturn(createdFee);

      final var result = this.createFeeImpl.execute(request);

      assertThat(result.lockerSize()).isEqualTo(size);
      assertThat(result.id()).isEqualTo(feeId);
      verify(this.feeCachePort).put(createdFee);
    }
  }

  @Test
  void execute_withMultipleCurrencies_shouldCreateFeeForEachCurrency() {
    final var currencies = new String[] {"ARS", "EUR", "USD", "GBP"};

    for (final String currency : currencies) {
      final var feeId = UUID.randomUUID();
      final var request = Fee
          .builder()
          .lockerSize(LockerSize.MEDIUM)
          .country("USA")
          .currency(currency)
          .price("20.00")
          .build();

      final var createdFee = Fee
          .builder()
          .id(feeId)
          .lockerSize(LockerSize.MEDIUM)
          .country("USA")
          .currency(currency)
          .price("20.00")
          .version(1L)
          .createdAt(Instant.now())
          .updatedAt(Instant.now())
          .build();

      when(this.feeRepositoryPort.insert(any(Fee.class))).thenReturn(createdFee);

      final var result = this.createFeeImpl.execute(request);

      assertThat(result.currency()).isEqualTo(currency);
      verify(this.feeCachePort).put(result);
    }
  }

  @Test
  void execute_withDecimalPrice_shouldPreservePrice() {
    final var feeId = UUID.randomUUID();
    final var price = "99.99";

    final var request = Fee
        .builder()
        .lockerSize(LockerSize.LARGE)
        .country("MEXICO")
        .currency("MXN")
        .price(price)
        .build();

    final var createdFee = Fee
        .builder()
        .id(feeId)
        .lockerSize(LockerSize.LARGE)
        .country("MEXICO")
        .currency("MXN")
        .price(price)
        .version(1L)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();

    when(this.feeRepositoryPort.insert(any(Fee.class))).thenReturn(createdFee);

    final var result = this.createFeeImpl.execute(request);

    assertThat(result.price()).isEqualTo(price);
    verify(this.feeCachePort).put(createdFee);
  }
}

