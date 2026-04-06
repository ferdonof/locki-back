package com.ferdonof.locki.specifications;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.ferdonof.locki.entities.FeeEntity;
import com.ferdonof.locki.fees.entities.Fee;
import com.ferdonof.locki.lockers.enums.LockerSize;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FeeSpecification {

  public static Specification<FeeEntity> fromFilter(Fee filter) {
    return Specification.allOf(
        hasId(filter.id()),
        hasCountry(filter.country()),
        hasCurrency(filter.currency()),
        hasLockerSize(filter.lockerSize())
    );
  }

  private static Specification<FeeEntity> hasId(UUID id) {
    return id == null
        ? null
        : (root, query, cb) -> cb.equal(root.get("id"), id);
  }

  private static Specification<FeeEntity> hasCountry(String country) {
    return country == null
        ? null
        : (root, query, cb) -> cb.equal(root.get("country"), country);
  }

  private static Specification<FeeEntity> hasCurrency(String currency) {
    return currency == null
        ? null
        : (root, query, cb) -> cb.equal(root.get("currency"), currency);
  }

  private static Specification<FeeEntity> hasLockerSize(LockerSize lockerSize) {
    return lockerSize == null
        ? null
        : (root, query, cb) -> cb.equal(root.get("lockerSize"), lockerSize);
  }
}

