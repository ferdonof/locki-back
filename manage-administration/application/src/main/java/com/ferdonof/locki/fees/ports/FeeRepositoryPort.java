package com.ferdonof.locki.fees.ports;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ferdonof.locki.fees.entities.Fee;

public interface FeeRepositoryPort {

  Fee insert(Fee fee);

  Fee update(Fee fee);

  void delete(UUID id);

  List<Fee> findFees(Fee fee);

  Optional<Fee> findById(UUID id);
}
