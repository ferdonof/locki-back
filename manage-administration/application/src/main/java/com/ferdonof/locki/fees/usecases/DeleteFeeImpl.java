package com.ferdonof.locki.fees.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.springframework.transaction.support.TransactionTemplate;

import com.ferdonof.locki.fees.ports.FeeRepositoryPort;

@Slf4j
@RequiredArgsConstructor
public class DeleteFeeImpl implements DeleteFee {

  private final TransactionTemplate transactionTemplate;

  private final FeeRepositoryPort feeRepository;

  @Override
  public void execute(UUID id) {
    this.transactionTemplate.execute(status -> {
      this.feeRepository.delete(id);
      return null;
    });
  }
}
