package com.ferdonof.locki.adapters;

import com.ferdonof.locki.entities.UserEntity;
import com.ferdonof.locki.repositories.UserRepository;
import com.ferdonof.locki.users.entities.LockiUser;
import com.ferdonof.locki.users.exceptions.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(UserRepositoryAdapter.class)
@ComponentScan(basePackages = "com.ferdonof.locki.mappers")
class UserRepositoryAdapterTestIT {

	private static final String NAME = "John Doe";

	private static final String PHONE = "+123456789";

	private static final String EMAIL = "john@example.com";

	@Autowired
	private UserRepositoryAdapter userRepositoryAdapter;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TestEntityManager entityManager;

	@BeforeEach
	void setUp() {
		this.userRepository.deleteAll();
	}

	@Test
	void whenUserNotExists_thenInsertUserAndPersistInDatabase() {
		final LockiUser lockiUser = new LockiUser(null, NAME, PHONE, EMAIL, null, null, null);

		final LockiUser result = this.userRepositoryAdapter.insert(lockiUser);

		assertThat(result.id()).isNotNull();
		assertThat(result.name()).isEqualTo(NAME);
		assertThat(result.email()).isEqualTo(EMAIL);
		assertThat(result.phone()).isEqualTo(PHONE);
		assertThat(result.createdAt()).isNotNull();
		assertThat(result.updatedAt()).isNotNull();

		this.entityManager.clear();

		final Optional<UserEntity> fromDb = this.userRepository.findById(result.id());

		assertThat(fromDb).isPresent();
		assertThat(fromDb.get().getName()).isEqualTo(NAME);
		assertThat(fromDb.get().getEmail()).isEqualTo(EMAIL);
		assertThat(fromDb.get().getPhone()).isEqualTo(PHONE);
		assertThat(fromDb.get().getCreatedAt()).isNotNull();
		assertThat(fromDb.get().getUpdatedAt()).isNotNull();
	}

	@Test
	void whenEntitiesAreOk_thenInsertUsersAndPersistAllInDatabase() {
		final LockiUser user1 = new LockiUser(null, "User One", "+111111111", "user1@example.com", null, null, null);
		final LockiUser user2 = new LockiUser(null, "User Two", "+222222222", "user2@example.com", null, null, null);

		this.userRepositoryAdapter.insert(user1);
		this.userRepositoryAdapter.insert(user2);

		this.entityManager.clear();

		assertThat(this.userRepository.count()).isEqualTo(2);
	}

	@Test
	void whenDuplicateEmail_thenThrowUserAlreadyExistsException() {
		final LockiUser firstUser = new LockiUser(null, NAME, PHONE, EMAIL, null, null, null);
		this.userRepositoryAdapter.insert(firstUser);

		final LockiUser duplicateUser = new LockiUser(null, "Jane Doe", "+987654321", EMAIL, null, null, null);

		assertThatThrownBy(() -> this.userRepositoryAdapter.insert(duplicateUser))
				.isInstanceOf(UserAlreadyExistsException.class).hasMessageContaining(EMAIL);

		this.entityManager.clear();
		assertThat(this.userRepository.count()).isEqualTo(1);
	}
}
