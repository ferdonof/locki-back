package com.ferdonof.locki.specifications;

import com.ferdonof.locki.entities.RackEntity;
import com.ferdonof.locki.racks.entity.RacksFilter;
import com.ferdonof.locki.racks.enums.RackStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RackSpecification {

	public static Specification<RackEntity> fromFilter(RacksFilter filter) {
		return Specification.allOf(
				hasId(filter.id()),
				hasStatus(filter.status()));
	}

	private static Specification<RackEntity> hasId(UUID id) {
		return id == null ? null : (root, query, cb) -> cb.equal(root.get("id"), id);
	}

	private static Specification<RackEntity> hasStatus(RackStatus status) {
		return status == null ? null : (root, query, cb) -> cb.equal(root.get("status"), status);
	}
}


