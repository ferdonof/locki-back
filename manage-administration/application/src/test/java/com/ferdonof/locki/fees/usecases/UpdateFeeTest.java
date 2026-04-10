package com.ferdonof.locki.fees.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ferdonof.locki.fees.entities.Fee;
import com.ferdonof.locki.fees.ports.FeeCachePort;
import com.ferdonof.locki.fees.ports.FeeRepositoryPort;
import com.ferdonof.locki.lockers.enums.LockerSize;

@ExtendWith(MockitoExtension.class)
class UpdateFeeTest {

  @Mock
  private TransactionTemplate txTemplate;

  @Mock
  private FeeRepositoryPort feeRepositoryPort;

  @Mock
  private FeeCachePort feeCachePort;

  @InjectMocks
  private UpdateFeeImpl updateFeeImpl;

  @BeforeEach
  void setup() {
    when(this.txTemplate.execute(any())).thenAnswer(invocation -> invocation
        .<TransactionCallback<?>>getArgument(0)
        .doInTransaction(mock(TransactionStatus.class)));
  }

  @Test
  void execute_whenValidFee_shouldUpdateFee() {
    final var feeId = UUID.randomUUID();
    final var createdAt = Instant
        .now()
        .minusSeconds(3600);
    final var updatedAt = Instant.now();

    final var existingFee = Fee
        .builder()
        .id(feeId)
        .lockerSize(LockerSize.MEDIUM)
        .country("SPAIN")
        .currency("EUR")
        .price(new BigDecimal("20.00"))
        .version(1L)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .build();

    final var request = Fee
        .builder()
        .id(feeId)
        .lockerSize(LockerSize.MEDIUM)
        .country("SPAIN")
        .currency("EUR")
        .price(new BigDecimal("25.50"))
        .build();

    final var updatedFee = Fee
        .builder()
        .id(feeId)
        .lockerSize(LockerSize.MEDIUM)
        .country("SPAIN")
        .currency("EUR")
        .price(new BigDecimal("25.50"))
        .version(2L)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .build();

    when(this.feeRepositoryPort.findById(feeId)).thenReturn(Optional.of(existingFee));
    when(this.feeRepositoryPort.update(any(Fee.class))).thenReturn(updatedFee);

    final var result = this.updateFeeImpl.execute(request);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(feeId);
    assertThat(result.lockerSize()).isEqualTo(LockerSize.MEDIUM);
    assertThat(result.country()).isEqualTo("SPAIN");
    assertThat(result.currency()).isEqualTo("EUR");
    assertThat(result.price()).isEqualTo("25.50");
    assertThat(result.version()).isEqualTo(2L);
    assertThat(result.createdAt()).isEqualTo(createdAt);

    verify(this.feeRepositoryPort).findById(feeId);
    verify(this.feeRepositoryPort).update(any(Fee.class));
    verify(this.feeCachePort).put(updatedFee);
  }

  @Test
  void execute_whenUpdatingPrice_shouldUpdateSuccessfully() {
    final var feeId = UUID.randomUUID();
    final var oldPrice = new BigDecimal("10.00");
    final var newPrice = new BigDecimal("15.50");

    final var existingFee = Fee
        .builder()
        .id(feeId)
        .lockerSize(LockerSize.SMALL)
        .country("ARGENTINA")
        .currency("ARS")
        .price(oldPrice)
        .version(1L)
        .build();

    final var request = Fee
        .builder()
        .id(feeId)
        .price(newPrice)
        .build();

    final var updatedFee = Fee
        .builder()
        .id(feeId)
        .lockerSize(LockerSize.SMALL)
        .country("ARGENTINA")
        .currency("ARS")
        .price(newPrice)
        .version(2L)
        .build();

    when(this.feeRepositoryPort.findById(feeId)).thenReturn(Optional.of(existingFee));
    when(this.feeRepositoryPort.update(any(Fee.class))).thenReturn(updatedFee);

    final var result = this.updateFeeImpl.execute(request);

    assertThat(result.price())
        .isEqualTo(newPrice)
        .isNotEqualTo(oldPrice);
    assertThat(result.version()).isGreaterThan(1L);

    verify(this.feeRepositoryPort).findById(feeId);
    verify(this.feeRepositoryPort).update(any(Fee.class));
    verify(this.feeCachePort).put(updatedFee);
  }

  @Test
  void execute_whenUpdatingLockerSize_shouldUpdateSuccessfully() {
    final var feeId = UUID.randomUUID();
    final var oldSize = LockerSize.SMALL;
    final var newSize = LockerSize.LARGE;

    final var existingFee = Fee
        .builder()
        .id(feeId)
        .lockerSize(oldSize)
        .country("MEXICO")
        .currency("MXN")
        .price(new BigDecimal("30.00"))
        .version(1L)
        .build();

    final var request = Fee
        .builder()
        .id(feeId)
        .lockerSize(newSize)
        .build();

    final var updatedFee = Fee
        .builder()
        .id(feeId)
        .lockerSize(newSize)
        .country("MEXICO")
        .currency("MXN")
        .price(new BigDecimal("30.00"))
        .version(2L)
        .build();

    when(this.feeRepositoryPort.findById(feeId)).thenReturn(Optional.of(existingFee));
    when(this.feeRepositoryPort.update(any(Fee.class))).thenReturn(updatedFee);

    final var result = this.updateFeeImpl.execute(request);

    assertThat(result.lockerSize())
        .isEqualTo(newSize)
        .isNotEqualTo(oldSize);

    verify(this.feeRepositoryPort).findById(feeId);
    verify(this.feeRepositoryPort).update(any(Fee.class));
    verify(this.feeCachePort).put(updatedFee);
  }

  @Test
  void execute_whenUpdatingCurrency_shouldUpdateSuccessfully() {
    final var feeId = UUID.randomUUID();
    final var oldCurrency = "ARS";
    final var newCurrency = "USD";

    final var existingFee = Fee
        .builder()
        .id(feeId)
        .lockerSize(LockerSize.MEDIUM)
        .country("USA")
        .currency(oldCurrency)
        .price(new BigDecimal("20.00"))
        .version(1L)
        .build();

    final var request = Fee
        .builder()
        .id(feeId)
        .currency(newCurrency)
        .build();

    final var updatedFee = Fee
        .builder()
        .id(feeId)
        .lockerSize(LockerSize.MEDIUM)
        .country("USA")
        .currency(newCurrency)
        .price(new BigDecimal("20.00"))
        .version(2L)
        .build();

    when(this.feeRepositoryPort.findById(feeId)).thenReturn(Optional.of(existingFee));
    when(this.feeRepositoryPort.update(any(Fee.class))).thenReturn(updatedFee);

    final var result = this.updateFeeImpl.execute(request);

    assertThat(result.currency())
        .isEqualTo(newCurrency)
        .isNotEqualTo(oldCurrency);

    verify(this.feeRepositoryPort).findById(feeId);
    verify(this.feeRepositoryPort).update(any(Fee.class));
    verify(this.feeCachePort).put(updatedFee);
  }

  @Test
  void execute_withHighVersion_shouldIncrementVersion() {
    final var feeId = UUID.randomUUID();

    final var existingFee = Fee
        .builder()
        .id(feeId)
        .lockerSize(LockerSize.SMALL)
        .country("CHILE")
        .currency("CLP")
        .price(new BigDecimal("5.00"))
        .version(10L)
        .build();

    final var request = Fee
        .builder()
        .id(feeId)
        .lockerSize(LockerSize.SMALL)
        .country("CHILE")
        .currency("CLP")
        .price(new BigDecimal("5.00"))
        .version(10L)
        .build();

    final var updatedFee = Fee
        .builder()
        .id(feeId)
        .lockerSize(LockerSize.SMALL)
        .country("CHILE")
        .currency("CLP")
        .price(new BigDecimal("5.00"))
        .version(11L)
        .build();

    when(this.feeRepositoryPort.findById(feeId)).thenReturn(Optional.of(existingFee));
    when(this.feeRepositoryPort.update(any(Fee.class))).thenReturn(updatedFee);

    final var result = this.updateFeeImpl.execute(request);

    assertThat(result.version())
        .isEqualTo(11L)
        .isGreaterThan(10L);

    verify(this.feeRepositoryPort).findById(feeId);
    verify(this.feeRepositoryPort).update(any(Fee.class));
    verify(this.feeCachePort).put(updatedFee);
  }

  @Test
  void execute_whenConcurrentUpdateOccurs_shouldPropagateExceptionAndAvoidCacheUpdate() {
    final var feeId = UUID.randomUUID();

    final var existingFee = Fee
        .builder()
        .id(feeId)
        .lockerSize(LockerSize.MEDIUM)
        .country("SPAIN")
        .currency("EUR")
        .price(new BigDecimal("20.00"))
        .version(2L)
        .build();

    final var staleRequest = Fee
        .builder()
        .id(feeId)
        .price(new BigDecimal("22.50"))
        .version(1L)
        .build();

    when(this.feeRepositoryPort.findById(feeId)).thenReturn(Optional.of(existingFee));
    when(this.feeRepositoryPort.update(any(Fee.class)))
        .thenThrow(new OptimisticLockingFailureException("Fee was updated concurrently"));

    assertThatThrownBy(() -> this.updateFeeImpl.execute(staleRequest))
        .isInstanceOf(OptimisticLockingFailureException.class)
        .hasMessageContaining("concurrently");

    verify(this.feeRepositoryPort).findById(feeId);
    verify(this.feeRepositoryPort).update(any(Fee.class));
    verify(this.feeCachePort, never()).put(any(Fee.class));
  }
}

