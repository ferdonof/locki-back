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
      final List<String> constraintNames = constraints.stream().map(ConstraintValidationsConstants::getValue).toList();
			if (ex.getCause() instanceof ConstraintViolationException cve
					&& constraintNames.contains(cve.getConstraintName())) {
				throw exceptionSupplier.get();
			}
			throw ex;
		}
	}
}

