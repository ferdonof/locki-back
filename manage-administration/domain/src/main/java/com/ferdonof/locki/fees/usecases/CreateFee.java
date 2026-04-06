package com.ferdonof.locki.fees.usecases;

import com.ferdonof.locki.fees.entities.Fee;

public interface CreateFee {
  Fee execute(Fee fee);
}
