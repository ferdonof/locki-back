package com.ferdonof.locki.fees.usecases;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

import com.ferdonof.locki.fees.ports.FeeRepositoryPort;

@ExtendWith(MockitoExtension.class)
class DeleteFeeTest {

  @Mock
  private TransactionStatus txStatus;

  @Mock
  private TransactionTemplate txTemplate;

  @Mock
  private FeeRepositoryPort feeRepositoryPort;

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

    this.deleteFeeImpl.execute(feeId);

    verify(this.feeRepositoryPort).delete(feeId);
  }

  @Test
  void execute_withMultipleIds_shouldDeleteEachFee() {
    final var feeId1 = UUID.randomUUID();
    final var feeId2 = UUID.randomUUID();
    final var feeId3 = UUID.randomUUID();

    this.deleteFeeImpl.execute(feeId1);
    this.deleteFeeImpl.execute(feeId2);
    this.deleteFeeImpl.execute(feeId3);

    verify(this.feeRepositoryPort).delete(feeId1);
    verify(this.feeRepositoryPort).delete(feeId2);
    verify(this.feeRepositoryPort).delete(feeId3);
  }

  @Test
  void execute_withDifferentUUIDs_shouldCallDeleteWithCorrectId() {

    final var uuidList = new UUID[] {
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID()
    };

    doNothing()
        .when(this.feeRepositoryPort)
        .delete(any(UUID.class));

    for (final UUID uuid : uuidList) {
      this.deleteFeeImpl.execute(uuid);
    }

    verify(this.feeRepositoryPort, times(3)).delete(any(UUID.class));
  }

  @Test
  void execute_shouldNotReturnAnything() {
    final var feeId = UUID.randomUUID();

    doNothing()
        .when(this.feeRepositoryPort)
        .delete(feeId);

    this.deleteFeeImpl.execute(feeId);

    verify(this.feeRepositoryPort).delete(feeId);
  }
}

