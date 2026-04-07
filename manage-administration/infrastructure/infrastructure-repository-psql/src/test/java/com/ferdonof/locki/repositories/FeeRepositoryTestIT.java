package com.ferdonof.locki.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import com.ferdonof.locki.entities.FeeEntity;
import com.ferdonof.locki.lockers.enums.LockerSize;

@Tag("integration")
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FeeRepositoryTestIT {

  @Autowired
  private FeeRepository feeRepository;

  @Autowired
  private TestEntityManager entityManager;

  @BeforeEach
  void setUp() {
    this.feeRepository.deleteAll();
  }

  @Test
  void whenUpdatingWithStaleVersion_thenThrowOptimisticLockingAndKeepFirstUpdate() {
    final FeeEntity initial = this.feeRepository.saveAndFlush(
        FeeEntity
            .builder()
            .lockerSize(LockerSize.MEDIUM)
            .country("SPAIN")
            .currency("EUR")
            .price(new BigDecimal("20.00"))
            .build());

    this.entityManager.clear();

    final FeeEntity staleSnapshot = this.feeRepository.findById(initial.getId()).orElseThrow();
    this.entityManager.detach(staleSnapshot);

    final FeeEntity currentSnapshot = this.feeRepository.findById(initial.getId()).orElseThrow();
    final FeeEntity firstUpdate = this.copyWithPrice(currentSnapshot, new BigDecimal("25.00"));
    final FeeEntity updated = this.feeRepository.saveAndFlush(firstUpdate);

    final FeeEntity staleUpdate = this.copyWithPrice(staleSnapshot, new BigDecimal("30.00"));

    assertThatThrownBy(() -> this.feeRepository.saveAndFlush(staleUpdate))
        .isInstanceOf(ObjectOptimisticLockingFailureException.class);

    this.entityManager.clear();
    final FeeEntity persisted = this.feeRepository.findById(initial.getId()).orElseThrow();

    assertThat(persisted.getPrice()).isEqualByComparingTo(new BigDecimal("25.00"));
    assertThat(persisted.getVersion()).isEqualTo(updated.getVersion());
  }

  private FeeEntity copyWithPrice(FeeEntity source, BigDecimal newPrice) {
    return FeeEntity
        .builder()
        .id(source.getId())
        .lockerSize(source.getLockerSize())
        .country(source.getCountry())
        .currency(source.getCurrency())
        .price(newPrice)
        .version(source.getVersion())
        .createdAt(source.getCreatedAt())
        .updatedAt(source.getUpdatedAt())
        .build();
  }
}

