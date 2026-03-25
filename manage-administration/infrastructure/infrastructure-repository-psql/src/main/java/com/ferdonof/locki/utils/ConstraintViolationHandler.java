package com.ferdonof.locki.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Supplier;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import com.ferdonof.locki.enums.ConstraintValidationsConstants;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConstraintViolationHandler {

	public static <T> T executeOrThrow(Supplier<T> operation,
			List<ConstraintValidationsConstants> constraints,
			Supplier<? extends RuntimeException> exceptionSupplier) {
		try {
			return operation.get();
		} catch (final DataIntegrityViolationException ex) {
			if (ex.getCause() instanceof ConstraintViolationException cve
					&& constraints.stream()
							.map(ConstraintValidationsConstants::getValue)
							.anyMatch(name -> name.equalsIgnoreCase(cve.getConstraintName()))) {
				throw exceptionSupplier.get();
			}
			throw ex;
		}
	}
}

