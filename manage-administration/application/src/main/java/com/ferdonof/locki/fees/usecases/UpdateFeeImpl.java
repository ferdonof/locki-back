package com.ferdonof.locki.fees.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

import org.springframework.transaction.support.TransactionTemplate;

import com.ferdonof.locki.fees.entities.Fee;
import com.ferdonof.locki.fees.exceptions.FeeNotFoundException;
import com.ferdonof.locki.fees.ports.FeeRepositoryPort;

@Slf4j
@RequiredArgsConstructor
public class UpdateFeeImpl implements UpdateFee {

  private final TransactionTemplate transactionTemplate;

  private final FeeRepositoryPort feeRepository;

  @Override
  public Fee execute(Fee fee) {
    return this.transactionTemplate.execute(status -> {
      final Fee feeToUpdate = this.feeRepository
          .findById(fee.id())
          .orElseThrow(() -> new FeeNotFoundException(fee.id()));

      return this.feeRepository.update(this.toUpdate(fee, feeToUpdate));
    });
  }

  private Fee toUpdate(Fee fee, Fee toUpdate) {
    final Fee.FeeBuilder feeBuilder = toUpdate.toBuilder();

    this.applyIfPresent(fee.lockerSize(), feeBuilder::lockerSize);
    this.applyIfPresent(fee.country(), feeBuilder::country);
    this.applyIfPresent(fee.currency(), feeBuilder::currency);
    this.applyIfPresent(fee.price(), feeBuilder::price);

    return feeBuilder.build();
  }

  private <T> void applyIfPresent(T value, Consumer<T> consumer) {
    if (value != null) {
      consumer.accept(value);
    }
  }
}
