package com.ferdonof.locki.users.usecases;

import com.ferdonof.locki.users.entities.LockiUser;
import com.ferdonof.locki.users.exceptions.UserNotFoundException;
import com.ferdonof.locki.users.ports.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserImplTest {

	@Mock
	private UserRepositoryPort userRepository;

	@InjectMocks
	private GetUserImpl getUserImpl;

	@Test
	void execute_whenUserExists_shouldReturnUser() {
		final var userId = UUID.randomUUID();
		final var user = LockiUser.builder()
				.id(userId)
				.name("John Doe")
				.email("john@example.com")
				.phone("+123456789")
				.createdAt(Instant.now())
				.build();

		when(this.userRepository.findById(userId)).thenReturn(Optional.of(user));

		final var result = this.getUserImpl.execute(userId);

		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(userId);
		assertThat(result.name()).isEqualTo("John Doe");
		assertThat(result.email()).isEqualTo("john@example.com");

		verify(this.userRepository).findById(userId);
	}

	@Test
	void execute_whenUserNotExists_shouldThrowUserNotFoundException() {
		final var userId = UUID.randomUUID();

		when(this.userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> this.getUserImpl.execute(userId))
				.isInstanceOf(UserNotFoundException.class)
				.hasMessage("User with id '%s' not found".formatted(userId));

		verify(this.userRepository).findById(userId);
	}
}

