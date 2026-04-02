package com.ferdonof.locki.lockers.usecases;

import com.ferdonof.locki.lockers.entities.Locker;
import com.ferdonof.locki.lockers.entities.LockersFilter;
import com.ferdonof.locki.lockers.ports.LockerRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class SearchLockersImpl implements SearchLockers {

	private final LockerRepositoryPort lockerRepository;

	@Override
	public List<Locker> execute(LockersFilter filter) {
		log.info("Searching lockers with limit {} and offset {}", filter.limit(), filter.offset());
		return this.lockerRepository.search(filter);
	}
}

