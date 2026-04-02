package com.ferdonof.locki.adapters;

import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.entities.LockersFilter;
import com.ferdonof.locki.lockers.ports.LockerRepositoryPort;
import com.ferdonof.locki.mappers.LockerMapper;
import com.ferdonof.locki.repositories.LockerRepository;
import com.ferdonof.locki.specifications.LockerSpecification;
import com.ferdonof.locki.utils.ConstraintViolationHandler;
import com.ferdonof.locki.utils.PaginationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LockerRepositoryAdapter implements LockerRepositoryPort {

	private final LockerMapper lockerMapper;

	private final LockerRepository lockerRepository;

	@Override
	public Locker insert(Locker locker) {
		final var lockerEntity = this.lockerMapper.toEntity(locker);
		return ConstraintViolationHandler.executeOrThrow(
				() -> this.lockerMapper.toDomain(this.lockerRepository.saveAndFlush(lockerEntity)), Map.of());
	}

	@Override
	public Optional<Locker> findById(UUID id) {
		return this.lockerRepository.findById(id).map(this.lockerMapper::toDomain);
	}

	@Override
	public Locker update(Locker locker) {
		final var lockerEntity = this.lockerMapper.toEntity(locker);
		return ConstraintViolationHandler.executeOrThrow(
				() -> this.lockerMapper.toDomain(this.lockerRepository.saveAndFlush(lockerEntity)), Map.of());
	}

	@Override
	public List<Locker> search(LockersFilter filter) {
		final var pageable = PaginationValidator.toPageable(filter.offset(), filter.limit());
		final var spec = LockerSpecification.fromFilter(filter);
		return this.lockerRepository.findAll(spec, pageable).getContent().stream().map(this.lockerMapper::toDomain)
				.toList();
	}

	@Override
	public List<Locker> findByRackId(UUID rackId) {
		return this.lockerRepository.findByRackId(rackId).stream().map(this.lockerMapper::toDomain).toList();
	}
}
