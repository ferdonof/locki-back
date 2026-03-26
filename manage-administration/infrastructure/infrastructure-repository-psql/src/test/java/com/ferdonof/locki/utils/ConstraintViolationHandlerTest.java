package com.ferdonof.locki.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import com.ferdonof.locki.commons.exceptions.GenericClientException;
import com.ferdonof.locki.enums.ConstraintValidationsConstants;
import com.ferdonof.locki.users.exceptions.UserAlreadyExistsException;

class ConstraintViolationHandlerTest {

	@Test
	void shouldReturnResultWhenNoExceptionIsThrown() {
		final String result = ConstraintViolationHandler.executeOrThrow(
				() -> "success",
				List.of(ConstraintValidationsConstants.UK_USERS_EMAIL),
				() -> new RuntimeException("should not be thrown"));

		assertThat(result).isEqualTo("success");
	}

	@Test
	void shouldThrowCustomExceptionWhenConstraintMatches() {
		final DataIntegrityViolationException dbException = new DataIntegrityViolationException("duplicate key",
				new ConstraintViolationException("duplicate key", new SQLException(), "uk_users_email"));

		assertThatThrownBy(() -> ConstraintViolationHandler.executeOrThrow(
				() -> { throw dbException; },
				List.of(ConstraintValidationsConstants.UK_USERS_EMAIL),
				() -> new IllegalStateException("duplicated email")))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("duplicated email");
	}

	@Test
	void shouldThrowCustomExceptionWhenAnyConstraintInListMatches() {
		final DataIntegrityViolationException dbException = new DataIntegrityViolationException("duplicate key",
				new ConstraintViolationException("duplicate key", new SQLException(), "uk_users_email"));

		assertThatThrownBy(() -> ConstraintViolationHandler.executeOrThrow(
				() -> { throw dbException; },
				List.of(ConstraintValidationsConstants.UK_USERS_EMAIL),
				() -> new IllegalStateException("matched")))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("matched");
	}

	@Test
	void shouldThrowUserAlreadyExistsExceptionWhenEmailConstraintMatches() {
		final String email = "john@example.com";
		final DataIntegrityViolationException dbException = new DataIntegrityViolationException("duplicate key",
				new ConstraintViolationException("duplicate key", new SQLException(), "uk_users_email"));

		assertThatThrownBy(() -> ConstraintViolationHandler.executeOrThrow(
				() -> { throw dbException; },
				List.of(ConstraintValidationsConstants.UK_USERS_EMAIL),
				() -> new UserAlreadyExistsException(email)))
				.isInstanceOf(UserAlreadyExistsException.class)
				.hasMessageContaining(email);
	}

	@Test
	void shouldThrowGenericClientExceptionWhenConstraintDoesNotMatch() {
		final DataIntegrityViolationException dbException = new DataIntegrityViolationException("other",
				new ConstraintViolationException("other", new SQLException(), "other_constraint"));

		assertThatThrownBy(() -> ConstraintViolationHandler.executeOrThrow(
				() -> { throw dbException; },
				List.of(ConstraintValidationsConstants.UK_USERS_EMAIL),
				() -> new IllegalStateException("should not be thrown")))
				.isInstanceOf(GenericClientException.class);
	}

	@Test
	void shouldThrowGenericClientExceptionWhenCauseIsNotConstraintViolation() {
		final DataIntegrityViolationException dbException = new DataIntegrityViolationException("not null",
				new RuntimeException("some other cause"));

		assertThatThrownBy(() -> ConstraintViolationHandler.executeOrThrow(
				() -> { throw dbException; },
				List.of(ConstraintValidationsConstants.UK_USERS_EMAIL),
				() -> new IllegalStateException("should not be thrown")))
				.isInstanceOf(GenericClientException.class);
	}

	@Test
	void shouldThrowGenericClientExceptionWhenCauseIsNull() {
		final DataIntegrityViolationException dbException = new DataIntegrityViolationException("no cause");

		assertThatThrownBy(() -> ConstraintViolationHandler.executeOrThrow(
				() -> { throw dbException; },
				List.of(ConstraintValidationsConstants.UK_USERS_EMAIL),
				() -> new IllegalStateException("should not be thrown")))
				.isInstanceOf(GenericClientException.class);
	}

	@Test
	void shouldMatchConstraintNameCaseInsensitively() {
		final DataIntegrityViolationException dbException = new DataIntegrityViolationException("duplicate key",
				new ConstraintViolationException("duplicate key", new SQLException(), "UK_USERS_EMAIL"));

		assertThatThrownBy(() -> ConstraintViolationHandler.executeOrThrow(
				() -> { throw dbException; },
				List.of(ConstraintValidationsConstants.UK_USERS_EMAIL),
				() -> new IllegalStateException("case insensitive match")))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("case insensitive match");
	}
}

