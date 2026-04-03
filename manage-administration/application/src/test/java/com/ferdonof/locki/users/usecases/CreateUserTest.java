package com.ferdonof.locki.users.usecases;

import com.ferdonof.locki.users.entities.LockiUser;
import com.ferdonof.locki.users.exceptions.UserAlreadyExistsException;
import com.ferdonof.locki.users.ports.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserTest {

	@Mock
	private TransactionTemplate txTemplate;

	@Mock
	private UserRepositoryPort userRepository;

	@InjectMocks
	private CreateUserImpl createUserImpl;

	@BeforeEach
	void setup() {
		when(this.txTemplate.execute(any())).thenAnswer(invocation -> invocation.<TransactionCallback<?>>getArgument(0)
				.doInTransaction(mock(TransactionStatus.class)));
	}

	@Test
	void execute_whenValidUser_shouldCreateUser() {
		final var userId = UUID.randomUUID();
		final var user = LockiUser.builder().id(userId).name("John Doe").email("john@example.com").phone("+123456789")
				.createdAt(Instant.now()).build();

		final var createdUser = LockiUser.builder().id(userId).name("John Doe").email("john@example.com")
				.phone("+123456789").createdAt(Instant.now()).build();

		when(this.userRepository.insert(any(LockiUser.class))).thenReturn(createdUser);

		final var result = this.createUserImpl.execute(user);

		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(userId);
		assertThat(result.name()).isEqualTo("John Doe");
		assertThat(result.email()).isEqualTo("john@example.com");

		verify(this.userRepository).insert(any(LockiUser.class));
	}

	@Test
	void execute_whenUserWithDuplicateEmail_shouldThrowUserAlreadyExistsException() {
		final var user = LockiUser.builder().name("John Doe").email("john@example.com").phone("+123456789").build();

		when(this.userRepository.insert(any(LockiUser.class)))
				.thenThrow(new UserAlreadyExistsException("john@example.com"));

		assertThatThrownBy(() -> this.createUserImpl.execute(user)).isInstanceOf(UserAlreadyExistsException.class)
				.hasMessage("User with email 'john@example.com' already exists");

		verify(this.userRepository).insert(any(LockiUser.class));
	}
}
