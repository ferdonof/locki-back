package com.ferdonof.locki.fees.usecases;

import java.util.List;
import java.util.UUID;

import com.ferdonof.locki.fees.entities.Fee;

public interface GetFeeList {
  List<Fee> execute(UUID id, String country, String currency);
}
