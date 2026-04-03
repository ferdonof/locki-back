package com.ferdonof.locki.users.usecases;

import com.ferdonof.locki.users.entities.LockiUser;
import com.ferdonof.locki.users.entities.UserFilter;
import com.ferdonof.locki.users.ports.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchUsersImplTest {

	@Mock
	private UserRepositoryPort userRepository;

	@InjectMocks
	private SearchUsersImpl searchUsersImpl;

	@Test
	void execute_whenUsersExist_shouldReturnList() {
		final var user1 = LockiUser.builder()
				.id(UUID.randomUUID())
				.name("John Doe")
				.email("john@example.com")
				.phone("+123456789")
				.createdAt(Instant.now())
				.build();

		final var user2 = LockiUser.builder()
				.id(UUID.randomUUID())
				.name("Jane Doe")
				.email("jane@example.com")
				.phone("+987654321")
				.createdAt(Instant.now())
				.build();

		final var filter = UserFilter.builder().limit(10).offset(0).build();
		final var users = List.of(user1, user2);

		when(this.userRepository.search(any(UserFilter.class))).thenReturn(users);

		final var result = this.searchUsersImpl.execute(filter);

		assertThat(result).isNotNull().hasSize(2);
		assertThat(result).containsExactlyInAnyOrder(user1, user2);

		verify(this.userRepository).search(any(UserFilter.class));
	}

	@Test
	void execute_whenNoUsersExist_shouldReturnEmptyList() {
		final var filter = UserFilter.builder().limit(10).offset(0).build();

		when(this.userRepository.search(any(UserFilter.class))).thenReturn(List.of());

		final var result = this.searchUsersImpl.execute(filter);

		assertThat(result).isNotNull().isEmpty();

		verify(this.userRepository).search(any(UserFilter.class));
	}

	@Test
	void execute_withPagination_shouldReturnPaginatedResults() {
		final var users = List.of(
				LockiUser.builder().id(UUID.randomUUID()).name("User 1").email("user1@example.com").build()
		);

		final var filter = UserFilter.builder().limit(1).offset(0).build();

		when(this.userRepository.search(filter)).thenReturn(users);

		final var result = this.searchUsersImpl.execute(filter);

		assertThat(result).hasSize(1);

		verify(this.userRepository).search(filter);
	}
}

