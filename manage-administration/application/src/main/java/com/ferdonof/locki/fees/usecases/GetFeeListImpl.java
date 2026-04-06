package com.ferdonof.locki.fees.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

import com.ferdonof.locki.fees.entities.Fee;
import com.ferdonof.locki.fees.ports.FeeRepositoryPort;

@Slf4j
@RequiredArgsConstructor
public class GetFeeListImpl implements GetFeeList {

  private final FeeRepositoryPort feeRepository;

  @Override
  public List<Fee> execute(UUID id, String country, String currency) {
    return this.feeRepository.findFees(Fee
        .builder()
        .id(id)
        .country(country)
        .currency(currency)
        .build());
  }
}
