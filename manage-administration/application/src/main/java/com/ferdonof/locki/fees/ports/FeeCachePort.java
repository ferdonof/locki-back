package com.ferdonof.locki.fees.ports;

import java.util.Optional;

import com.ferdonof.locki.fees.entities.Fee;

public interface FeeCachePort {
  void put(Fee fee);

  Optional<Fee> get(Fee fee);

  void evictCache(Fee fee);
}
