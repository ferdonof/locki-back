package com.ferdonof.locki.adapters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.ferdonof.locki.fees.entities.Fee;
import com.ferdonof.locki.fees.ports.FeeRepositoryPort;
import com.ferdonof.locki.mappers.FeeMapper;
import com.ferdonof.locki.repositories.FeeRepository;
import com.ferdonof.locki.specifications.FeeSpecification;
import com.ferdonof.locki.utils.ConstraintViolationHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeeRepositoryAdapter implements FeeRepositoryPort {

  private final FeeMapper feeMapper;

  private final FeeRepository feeRepository;

  @Override
  public Fee insert(Fee fee) {
    final var feeEntity = this.feeMapper.toEntity(fee);
    return ConstraintViolationHandler.executeOrThrow(
        () -> this.feeMapper.toDomain(this.feeRepository.saveAndFlush(feeEntity)), Map.of());
  }

  @Override
  public Fee update(Fee fee) {
    final var feeEntity = this.feeMapper.toEntity(fee);
    return ConstraintViolationHandler.executeOrThrow(
        () -> this.feeMapper.toDomain(this.feeRepository.saveAndFlush(feeEntity)), Map.of());
  }

  @Override
  public void delete(UUID id) {
    this.feeRepository.deleteById(id);
  }

  @Override
  public List<Fee> findFees(Fee fee) {
    final var spec = FeeSpecification.fromFilter(fee);
    return this.feeRepository
        .findAll(spec)
        .stream()
        .map(this.feeMapper::toDomain)
        .toList();
  }

  @Override
  public Optional<Fee> findById(UUID id) {
    return this.feeRepository
        .findById(id)
        .map(this.feeMapper::toDomain);
  }
}
