package com.ferdonof.locki.racks.usecases;

import com.ferdonof.locki.racks.entity.Rack;
import com.ferdonof.locki.racks.entity.RacksFilter;

import java.util.List;

public interface SearchRacks {
	List<Rack> execute(RacksFilter filter);
}

