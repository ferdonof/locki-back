package com.ferdonof.locki.config;

import reservations.ports.RackedLockersRepositoryPort;
import reservations.ports.ReservationsRepositoryPort;
import reservations.usecases.CreateReservationImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

import com.ferdonof.locki.fee.ports.FeeCachePort;
import com.ferdonof.locki.reservations.usecases.CreateReservation;

@Configuration
public class ReservationsConfig {

  @Bean
  public CreateReservation createReservation(TransactionTemplate transactionTemplate, FeeCachePort feeCachePort,
      final RackedLockersRepositoryPort rackedLockersRepositoryPort, final ReservationsRepositoryPort reservationsRepositoryPort) {
    return new CreateReservationImpl(transactionTemplate, feeCachePort, rackedLockersRepositoryPort, reservationsRepositoryPort);
  }
}
