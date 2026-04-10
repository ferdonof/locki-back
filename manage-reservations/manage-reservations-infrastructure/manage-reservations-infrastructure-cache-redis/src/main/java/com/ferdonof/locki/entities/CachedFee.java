package com.ferdonof.locki.entities;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder(toBuilder = true)
public record CachedFee(UUID id, String country, String currency, String lockerSize, BigDecimal price) {
}
