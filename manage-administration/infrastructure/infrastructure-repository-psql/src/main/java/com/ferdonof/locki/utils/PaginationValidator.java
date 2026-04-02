package com.ferdonof.locki.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationValidator {

	public static Pageable toPageable(int offset, int limit) {
		if (limit <= 0) {
			throw new IllegalArgumentException("Limit must be greater than 0");
		}
		if (offset < 0) {
			throw new IllegalArgumentException("Offset must be greater than or equal to 0");
		}
		return PageRequest.of(offset / limit, limit);
	}
}
