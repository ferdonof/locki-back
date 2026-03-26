package com.ferdonof.locki.repositories;

import com.ferdonof.locki.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTestIT {

	public static final String NAME = "John Doe";

	public static final String EMAIL = "john@example.com";

	public static final String PHONE = "+123456789";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TestEntityManager entityManager;

	@BeforeEach
	void setUp() {
		this.userRepository.deleteAll();
	}

	@Test
	void whenSaveEntity_thenSaveAndFindUserById() {
		final UserEntity user = UserEntity.builder().name(NAME).email(EMAIL).phone(PHONE).build();

		final UserEntity saved = this.userRepository.save(user);
		this.entityManager.flush();

		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getCreatedAt()).isNotNull();
		assertThat(saved.getUpdatedAt()).isNotNull();

		final Optional<UserEntity> found = this.userRepository.findById(saved.getId());

		assertThat(found).isPresent();
		assertThat(found.get().getName()).isEqualTo(NAME);
		assertThat(found.get().getEmail()).isEqualTo(EMAIL);
		assertThat(found.get().getPhone()).isEqualTo(PHONE);
	}

	@Test
	void whenUserNotFound_thenReturnEmpty() {
		final Optional<UserEntity> found = this.userRepository.findById(UUID.randomUUID());

		assertThat(found).isEmpty();
	}

	@Test
	void shouldDeleteUser() {
		final UserEntity user = UserEntity.builder().name("Jane Doe").email("jane@example.com").phone("+987654321")
				.build();

		final UserEntity saved = this.userRepository.save(user);

		this.userRepository.deleteById(saved.getId());

		assertThat(this.userRepository.findById(saved.getId())).isEmpty();
	}

	@Test
	void whenInsertedEntitiesAreOk_thenFindAllUsers() {
		final UserEntity user1 = UserEntity.builder().name("User One").email("user1@example.com").phone("+111111111")
				.build();

		final UserEntity user2 = UserEntity.builder().name("User Two").email("user2@example.com").phone("+222222222")
				.build();

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