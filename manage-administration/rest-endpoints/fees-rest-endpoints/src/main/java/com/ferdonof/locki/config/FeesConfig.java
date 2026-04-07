package com.ferdonof.locki.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

import com.ferdonof.locki.fees.ports.FeeCachePort;
import com.ferdonof.locki.fees.ports.FeeRepositoryPort;
import com.ferdonof.locki.fees.usecases.CreateFee;
import com.ferdonof.locki.fees.usecases.CreateFeeImpl;
import com.ferdonof.locki.fees.usecases.DeleteFee;
import com.ferdonof.locki.fees.usecases.DeleteFeeImpl;
import com.ferdonof.locki.fees.usecases.GetFeeList;
import com.ferdonof.locki.fees.usecases.GetFeeListImpl;
import com.ferdonof.locki.fees.usecases.UpdateFee;
import com.ferdonof.locki.fees.usecases.UpdateFeeImpl;

@Configuration
public class FeesConfig {

  @Bean
  public CreateFee createFee(final TransactionTemplate transactionTemplate, final FeeRepositoryPort feeRepositoryPort,
      final FeeCachePort feeCachePort) {
    return new CreateFeeImpl(transactionTemplate, feeRepositoryPort, feeCachePort);
  }

  @Bean
  public DeleteFee deleteFee(final TransactionTemplate transactionTemplate, final FeeRepositoryPort feeRepositoryPort,
      final FeeCachePort feeCachePort) {
    return new DeleteFeeImpl(transactionTemplate, feeRepositoryPort, feeCachePort);
  }

  @Bean
  public UpdateFee updateFee(final TransactionTemplate transactionTemplate, final FeeRepositoryPort feeRepositoryPort,
      final FeeCachePort feeCachePort) {
    return new UpdateFeeImpl(transactionTemplate, feeRepositoryPort, feeCachePort);
  }

  @Bean
  public GetFeeList getFeeList(final FeeRepositoryPort feeRepositoryPort) {
    return new GetFeeListImpl(feeRepositoryPort);
  }
}
