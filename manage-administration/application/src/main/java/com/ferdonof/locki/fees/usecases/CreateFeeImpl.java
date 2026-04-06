package com.ferdonof.locki.fees.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.transaction.support.TransactionTemplate;

import com.ferdonof.locki.fees.entities.Fee;
import com.ferdonof.locki.fees.ports.FeeRepositoryPort;

@Slf4j
@RequiredArgsConstructor
public class CreateFeeImpl implements CreateFee {

  private final TransactionTemplate transactionTemplate;

  private final FeeRepositoryPort feeRepository;

  @Override
  public Fee execute(Fee fee) {
    log.info("Creating fee with lockerSize: {}, country: {}, currency: {}", fee.lockerSize(), fee.country(), fee.currency());
    return this.transactionTemplate.execute(status -> this.feeRepository.insert(fee));
  }
}
