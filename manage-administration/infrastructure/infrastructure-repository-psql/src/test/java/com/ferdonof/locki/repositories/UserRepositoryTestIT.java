package com.ferdonof.locki.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.ferdonof.locki.entities.UserEntity;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTestIT {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TestEntityManager entityManager;

	@BeforeEach
	void setUp() {
		this.userRepository.deleteAll();
	}

	@Test
	void shouldSaveAndFindUserById() {
		final UserEntity user = UserEntity.builder().name("John Doe").email("john@example.com").phone("+123456789").build();

		final UserEntity saved = this.userRepository.save(user);
		this.entityManager.flush();

		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getCreatedAt()).isNotNull();
		assertThat(saved.getUpdatedAt()).isNotNull();

		final Optional<UserEntity> found = this.userRepository.findById(saved.getId());

		assertThat(found).isPresent();
		assertThat(found.get().getName()).isEqualTo("John Doe");
		assertThat(found.get().getEmail()).isEqualTo("john@example.com");
		assertThat(found.get().getPhone()).isEqualTo("+123456789");
	}

	@Test
	void shouldReturnEmptyWhenUserNotFound() {
		final Optional<UserEntity> found = this.userRepository.findById(UUID.randomUUID());

		assertThat(found).isEmpty();
	}

	@Test
	void shouldDeleteUser() {
		final UserEntity user = UserEntity.builder().name("Jane Doe").email("jane@example.com").phone("+987654321").build();

		final UserEntity saved = this.userRepository.save(user);

		this.userRepository.deleteById(saved.getId());

		assertThat(this.userRepository.findById(saved.getId())).isEmpty();
	}

	@Test
	void shouldFindAllUsers() {
		final UserEntity user1 = UserEntity.builder().name("User One").email("user1@example.com").phone("+111111111").build();

		final UserEntity user2 = UserEntity.builder().name("User Two").email("user2@example.com").phone("+222222222").build();

		this.userRepository.save(user1);
		this.userRepository.save(user2);

		final Iterable<UserEntity> users = this.userRepository.findAll();

		assertThat(users).hasSize(2);
	}

	@Test
	void shouldCountUsers() {
		final UserEntity user = UserEntity.builder().name("Count User").email("count@example.com").phone("+333333333")
				.build();

		this.userRepository.save(user);

		assertThat(this.userRepository.count()).isEqualTo(1);
	}
}