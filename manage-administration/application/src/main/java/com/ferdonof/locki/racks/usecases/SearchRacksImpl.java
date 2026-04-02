package com.ferdonof.locki.racks.usecases;

import com.ferdonof.locki.racks.entity.Rack;
import com.ferdonof.locki.racks.entity.RacksFilter;
import com.ferdonof.locki.racks.ports.RackRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class SearchRacksImpl implements SearchRacks {

	private final RackRepositoryPort rackRepository;

	@Override
	public List<Rack> execute(RacksFilter filter) {
		log.info("Searching racks with limit {} and offset {}", filter.limit(), filter.offset());
		return this.rackRepository.search(filter);
	}
}

