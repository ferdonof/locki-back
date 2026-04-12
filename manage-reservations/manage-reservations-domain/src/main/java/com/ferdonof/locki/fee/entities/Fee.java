package com.ferdonof.locki.fee.entities;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

import com.ferdonof.locki.lockers.enums.LockerSize;

@Builder(toBuilder = true)
public record Fee(UUID id, LockerSize lockerSize, String country, String currency, BigDecimal price) {
}
