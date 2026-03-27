package com.ferdonof.locki.utils;

import com.ferdonof.locki.commons.exceptions.GenericClientException;
import com.ferdonof.locki.enums.ConstraintValidationsConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Map;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConstraintViolationHandler {

	public static <T> T executeOrThrow(Supplier<T> operation,
		 Map<ConstraintValidationsConstants, Supplier<? extends RuntimeException>> constraints) {

		try {
			return operation.get();
		} catch (final DataIntegrityViolationException ex) {
			if (ex.getCause() instanceof ConstraintViolationException cve) {
				final var key = ConstraintValidationsConstants.from(cve.getConstraintName());
				if (key != null && constraints.containsKey(key)) {
					throw constraints.get(key).get();
				}
			}
			throw new GenericClientException();
		}
	}
}

