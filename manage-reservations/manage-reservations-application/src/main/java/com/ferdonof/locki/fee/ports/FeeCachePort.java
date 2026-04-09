package com.ferdonof.locki.fee.ports;

import java.util.Optional;

import com.ferdonof.locki.fee.entities.Fee;

public interface FeeCachePort {
  void put(Fee fee);

  Optional<Fee> get(Fee fee);

  void evictCache(Fee fee);
}
