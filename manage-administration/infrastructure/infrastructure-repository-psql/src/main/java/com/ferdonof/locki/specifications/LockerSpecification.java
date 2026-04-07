package com.ferdonof.locki.specifications;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.ferdonof.locki.entities.LockerEntity;
import com.ferdonof.locki.lockers.entities.LockersFilter;
import com.ferdonof.locki.lockers.enums.LatchStatus;
import com.ferdonof.locki.lockers.enums.LockerStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LockerSpecification {

  public static Specification<LockerEntity> fromFilter(LockersFilter filter) {
    return Specification.allOf(
        hasId(filter.id()),
        hasNumber(filter.serial()),
        hasRackId(filter.rackId()),
        hasStatus(filter.status()),
        hasLatchStatus(filter.latchStatus())
    );
  }

  private static Specification<LockerEntity> hasId(UUID id) {
    return id == null
        ? null
        : (root, query, cb) -> cb.equal(root.get("id"), id);
  }

  private static Specification<LockerEntity> hasNumber(Integer number) {
    return number == null
        ? null
        : (root, query, cb) -> cb.equal(root.get("serial"), number);
  }

  private static Specification<LockerEntity> hasRackId(UUID rackId) {
    return rackId == null
        ? null
        : (root, query, cb) -> cb.equal(root
                                        .get("rack")
                                        .get("id"), rackId);
  }

  private static Specification<LockerEntity> hasStatus(LockerStatus status) {
    return status == null
        ? null
        : (root, query, cb) -> cb.equal(root.get("status"), status);
  }

  private static Specification<LockerEntity> hasLatchStatus(LatchStatus latchStatus) {
    return latchStatus == null
        ? null
        : (root, query, cb) -> cb.equal(root.get("latchStatus"), latchStatus);
  }
}
