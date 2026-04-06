package com.ferdonof.locki.fees.usecases;

import com.ferdonof.locki.fees.entities.Fee;

public interface UpdateFee {
  Fee execute(Fee fee);
}
