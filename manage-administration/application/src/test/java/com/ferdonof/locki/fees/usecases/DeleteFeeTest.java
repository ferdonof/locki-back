package com.ferdonof.locki.fees.usecases;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import com.ferdonof.locki.fees.entities.Fee;
import com.ferdonof.locki.fees.ports.FeeCachePort;
import com.ferdonof.locki.fees.ports.FeeRepositoryPort;
import com.ferdonof.locki.lockers.enums.LockerSize;

@ExtendWith(MockitoExtension.class)
class DeleteFeeTest {

  @Mock
  private TransactionStatus txStatus;

  @Mock
  private TransactionTemplate txTemplate;

  @Mock
  private FeeRepositoryPort feeRepositoryPort;

  @Mock
  private FeeCachePort feeCachePort;

  @InjectMocks
  private DeleteFeeImpl deleteFeeImpl;

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {
    doAnswer(invocation -> {
      final Consumer<TransactionStatus> callback = invocation.getArgument(0);
      callback.accept(this.txStatus);
      return null;
    })
        .when(this.txTemplate)
        .executeWithoutResult(any(Consumer.class));
  }

  @Test
  void execute_withValidId_shouldDeleteFee() {
    final var feeId = UUID.randomUUID();

    final Fee fee = this.buildFee(feeId);
    when(this.feeRepositoryPort.findById(feeId)).thenReturn(Optional.of(fee));

    this.deleteFeeImpl.execute(feeId);

    verify(this.feeRepositoryPort).findById(feeId);
    verify(this.feeRepositoryPort).delete(feeId);
    verify(this.feeCachePort).evictCache(fee);
  }

  @Test
  void execute_withMultipleIds_shouldDeleteEachFee() {
    final var feeId1 = UUID.randomUUID();
    final var feeId2 = UUID.randomUUID();
    final var feeId3 = UUID.randomUUID();

    final Fee fee1 = this.buildFee(feeId1);
    final Fee fee2 = this.buildFee(feeId2);
    final Fee fee3 = this.buildFee(feeId3);

    when(this.feeRepositoryPort.findById(any(UUID.class)))
        .thenReturn(Optional.ofNullable(fee1))
        .thenReturn(Optional.ofNullable(fee2))
        .thenReturn(Optional.ofNullable(fee3));

    this.deleteFeeImpl.execute(feeId1);
    this.deleteFeeImpl.execute(feeId2);
    this.deleteFeeImpl.execute(feeId3);

    verify(this.feeRepositoryPort, times(3)).findById(any(UUID.class));

    verify(this.feeRepositoryPort).delete(feeId1);
    verify(this.feeRepositoryPort).delete(feeId2);
    verify(this.feeRepositoryPort).delete(feeId3);

    verify(this.feeCachePort).evictCache(fee1);
    verify(this.feeCachePort).evictCache(fee2);
    verify(this.feeCachePort).evictCache(fee3);
  }

  @Test
  void execute_withDifferentUUIDs_shouldCallDeleteWithCorrectId() {

    final var uuidList = new UUID[] {
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID()
    };

    when(this.feeRepositoryPort.findById(any(UUID.class)))
        .thenReturn(Optional.of(mock(Fee.class)))
        .thenReturn(Optional.of(mock(Fee.class)))
        .thenReturn(Optional.of(mock(Fee.class)));

    doNothing()
        .when(this.feeRepositoryPort)
        .delete(any(UUID.class));

    for (final UUID uuid : uuidList) {
      this.deleteFeeImpl.execute(uuid);
    }

    verify(this.feeRepositoryPort, times(3)).findById(any(UUID.class));
    verify(this.feeRepositoryPort, times(3)).delete(any(UUID.class));
    verify(this.feeCachePort, times(3)).evictCache(any(Fee.class));
  }

  private Fee buildFee(UUID id) {
    final Instant now = Instant.now();
    return Fee
        .builder()
        .id(id)
        .lockerSize(LockerSize.SMALL)
        .country("ARGENTINA")
        .currency("ARS")
        .price(new BigDecimal("10.50"))
        .version(1L)
        .createdAt(now)
        .updatedAt(now)
        .build();
  }
}

