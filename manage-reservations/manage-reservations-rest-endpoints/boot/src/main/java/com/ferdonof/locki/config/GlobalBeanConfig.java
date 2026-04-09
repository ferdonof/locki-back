package com.ferdonof.locki.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class GlobalBeanConfig {

  @Bean
  public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
    final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
    transactionTemplate.setIsolationLevel(TransactionTemplate.ISOLATION_REPEATABLE_READ);
    return transactionTemplate;
  }
}
