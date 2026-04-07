package com.ferdonof.locki.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;

import com.ferdonof.locki.commons.exceptions.GenericClientException;
import com.ferdonof.locki.entities.FeeEntity;
import com.ferdonof.locki.fees.entities.Fee;
import com.ferdonof.locki.lockers.enums.LockerSize;
import com.ferdonof.locki.mappers.FeeMapper;
import com.ferdonof.locki.repositories.FeeRepository;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class FeeRepositoryAdapterTest {

  private static final String COUNTRY = "ARGENTINA";

  private static final String CURRENCY = "ARS";

  @Mock
  private FeeRepository feeRepository;

  @Mock
  private FeeMapper feeMapper;

  @InjectMocks
  private FeeRepositoryAdapter feeRepositoryAdapter;

  @Test
  void insert_whenValidFee_shouldInsertAndReturnDomain() {
    final var fee = this.buildFee(null);
    final var entity = this.buildFeeEntity();
    final var expected = this.buildFee(entity.getId());

    when(this.feeMapper.toEntity(fee)).thenReturn(entity);
    when(this.feeRepository.saveAndFlush(entity)).thenReturn(entity);
    when(this.feeMapper.toDomain(entity)).thenReturn(expected);

    final var result = this.feeRepositoryAdapter.insert(fee);

    assertThat(result).isEqualTo(expected);
    assertThat(result.id()).isNotNull();
    assertThat(result.country()).isEqualTo(COUNTRY);
    assertThat(result.currency()).isEqualTo(CURRENCY);
    assertThat(result.lockerSize()).isEqualTo(LockerSize.SMALL);
    verify(this.feeMapper).toEntity(fee);
    verify(this.feeRepository).saveAndFlush(entity);
    verify(this.feeMapper).toDomain(entity);
  }

  @Test
  void insert_whenUniqueConstraintViolated_shouldThrowGenericClientException() {
    final var fee = this.buildFee(null);
    final var entity = this.buildFeeEntity();

    when(this.feeMapper.toEntity(fee)).thenReturn(entity);
    when(this.feeRepository.saveAndFlush(entity)).thenThrow(
        new DataIntegrityViolationException("duplicate",
            new ConstraintViolationException("duplicate", new SQLException(),
                "uk_fees_locker_size_country_currency")));

    assertThatThrownBy(() -> this.feeRepositoryAdapter.insert(fee))
        .isInstanceOf(GenericClientException.class);
  }

  @Test
  void insert_whenCauseIsNotConstraintViolation_shouldThrowGenericClientException() {
    final var fee = this.buildFee(null);
    final var entity = this.buildFeeEntity();

    when(this.feeMapper.toEntity(fee)).thenReturn(entity);
    when(this.feeRepository.saveAndFlush(entity)).thenThrow(
        new DataIntegrityViolationException("not null", new RuntimeException("some cause")));

    assertThatThrownBy(() -> this.feeRepositoryAdapter.insert(fee))
        .isInstanceOf(GenericClientException.class);
  }

  @Test
  void update_whenValidFee_shouldUpdateAndReturnDomain() {
    final var feeId = UUID.randomUUID();
    final var fee = this.buildFee(feeId);
    final var entity = this.buildFeeEntity();
    final var expected = this.buildFee(feeId);

    when(this.feeMapper.toEntity(fee)).thenReturn(entity);
    when(this.feeRepository.saveAndFlush(entity)).thenReturn(entity);
    when(this.feeMapper.toDomain(entity)).thenReturn(expected);

    final var result = this.feeRepositoryAdapter.update(fee);

    assertThat(result).isEqualTo(expected);
    assertThat(result.id()).isEqualTo(feeId);
    verify(this.feeMapper).toEntity(fee);
    verify(this.feeRepository).saveAndFlush(entity);
    verify(this.feeMapper).toDomain(entity);
  }

  @Test
  void update_whenUniqueConstraintViolated_shouldThrowGenericClientException() {
    final var fee = this.buildFee(UUID.randomUUID());
    final var entity = this.buildFeeEntity();

    when(this.feeMapper.toEntity(fee)).thenReturn(entity);
    when(this.feeRepository.saveAndFlush(entity)).thenThrow(
        new DataIntegrityViolationException("duplicate",
            new ConstraintViolationException("duplicate", new SQLException(),
                "uk_fees_locker_size_country_currency")));

    assertThatThrownBy(() -> this.feeRepositoryAdapter.update(fee))
        .isInstanceOf(GenericClientException.class);
  }

  @Test
  void update_whenCauseIsNotConstraintViolation_shouldThrowGenericClientException() {
    final var fee = this.buildFee(UUID.randomUUID());
    final var entity = this.buildFeeEntity();

    when(this.feeMapper.toEntity(fee)).thenReturn(entity);
    when(this.feeRepository.saveAndFlush(entity)).thenThrow(
        new DataIntegrityViolationException("not null", new RuntimeException("some cause")));

    assertThatThrownBy(() -> this.feeRepositoryAdapter.update(fee))
        .isInstanceOf(GenericClientException.class);
  }

  @Test
  void delete_whenValidId_shouldDelegateToRepository() {
    final var id = UUID.randomUUID();

    this.feeRepositoryAdapter.delete(id);

    verify(this.feeRepository).deleteById(id);
  }

  @Test
  void delete_whenCalledMultipleTimes_shouldDelegateEachCall() {
    final var id1 = UUID.randomUUID();
    final var id2 = UUID.randomUUID();

    this.feeRepositoryAdapter.delete(id1);
    this.feeRepositoryAdapter.delete(id2);

    verify(this.feeRepository).deleteById(id1);
    verify(this.feeRepository).deleteById(id2);
  }

  @Test
  @SuppressWarnings("unchecked")
  void findFees_whenResultsExist_shouldReturnMappedDomainList() {
    final var filter = this.buildFee(null);
    final var entity1 = this.buildFeeEntity();
    final var entity2 = this.buildFeeEntity();
    final var domain1 = this.buildFee(entity1.getId());
    final var domain2 = this.buildFee(entity2.getId());

    when(this.feeRepository.findAll(any(Specification.class))).thenReturn(List.of(entity1, entity2));
    when(this.feeMapper.toDomain(entity1)).thenReturn(domain1);
    when(this.feeMapper.toDomain(entity2)).thenReturn(domain2);

    final var result = this.feeRepositoryAdapter.findFees(filter);

    assertThat(result)
        .hasSize(2)
        .containsExactlyInAnyOrder(domain1, domain2);
    verify(this.feeRepository).findAll(any(Specification.class));
    verify(this.feeMapper).toDomain(entity1);
    verify(this.feeMapper).toDomain(entity2);
  }

  @Test
  @SuppressWarnings("unchecked")
  void findFees_whenNoResults_shouldReturnEmptyList() {
    final var filter = this.buildFee(null);

    when(this.feeRepository.findAll(any(Specification.class))).thenReturn(List.of());

    final var result = this.feeRepositoryAdapter.findFees(filter);

    assertThat(result).isEmpty();
    verify(this.feeRepository).findAll(any(Specification.class));
  }

  @Test
  @SuppressWarnings("unchecked")
  void findFees_withNullFilterFields_shouldReturnAllFees() {
    final var filter = Fee
        .builder()
        .build();
    final var entity = this.buildFeeEntity();
    final var domain = this.buildFee(entity.getId());

    when(this.feeRepository.findAll(any(Specification.class))).thenReturn(List.of(entity));
    when(this.feeMapper.toDomain(entity)).thenReturn(domain);

    final var result = this.feeRepositoryAdapter.findFees(filter);

    assertThat(result).hasSize(1);
    verify(this.feeRepository).findAll(any(Specification.class));
  }

  private Fee buildFee(UUID id) {
    return Fee
        .builder()
        .id(id)
        .lockerSize(LockerSize.SMALL)
        .country(COUNTRY)
        .currency(CURRENCY)
        .price("23.50")
        .version(1L)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
  }

  private FeeEntity buildFeeEntity() {
    return FeeEntity
        .builder()
        .id(UUID.randomUUID())
        .lockerSize(LockerSize.SMALL)
        .country(COUNTRY)
        .currency(CURRENCY)
        .price(new BigDecimal("23.50"))
        .build();
  }
}

