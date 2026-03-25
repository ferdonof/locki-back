package com.ferdonof.locki.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.ferdonof.locki.entities.UserEntity;
import com.ferdonof.locki.mappers.UserMapper;
import com.ferdonof.locki.repositories.UserRepository;
import com.ferdonof.locki.users.entities.LockiUser;
import com.ferdonof.locki.users.exceptions.UserAlreadyExistsException;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

	public static final String NAME = "John Doe";

	public static final String PHONE = "+123456789";

	public static final String EMAIL = "john@example.com";

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserMapper userMapper;

	@InjectMocks
	private UserRepositoryAdapter userRepositoryAdapter;

	@Test
	void shouldInsertUserSuccessfully() {
		final LockiUser lockiUser = new LockiUser(null, NAME, PHONE, EMAIL, null, null, null);
		final UserEntity entity = this.buildUserEntity();
		final LockiUser expectedResult = new LockiUser(entity.getId(), NAME, PHONE, EMAIL,
				entity.getVersion(), entity.getCreatedAt(), entity.getUpdatedAt());

		when(this.userMapper.toEntity(lockiUser)).thenReturn(entity);
		when(this.userRepository.saveAndFlush(entity)).thenReturn(entity);
		when(this.userMapper.toDomain(entity)).thenReturn(expectedResult);

		final LockiUser result = this.userRepositoryAdapter.insert(lockiUser);

		assertThat(result).isEqualTo(expectedResult);
		assertThat(result.id()).isNotNull();
		assertThat(result.email()).isEqualTo(EMAIL);
		verify(this.userMapper).toEntity(lockiUser);
		verify(this.userRepository).saveAndFlush(entity);
		verify(this.userMapper).toDomain(entity);
	}

	@Test
	void shouldThrowUserAlreadyExistsExceptionWhenEmailIsDuplicated() {
		final LockiUser lockiUser = new LockiUser(null, NAME, PHONE, EMAIL, null, null, null);
		final UserEntity entity = this.buildUserEntity();

		when(this.userMapper.toEntity(lockiUser)).thenReturn(entity);
		when(this.userRepository.saveAndFlush(entity)).thenThrow(
				new DataIntegrityViolationException("duplicate key",
				new ConstraintViolationException("duplicate key", new SQLException(), "uk_users_email")));

		assertThatThrownBy(() -> this.userRepositoryAdapter.insert(lockiUser))
				.isInstanceOf(UserAlreadyExistsException.class)
				.hasMessageContaining(EMAIL);
	}

	@Test
	void shouldRethrowDataIntegrityViolationExceptionWhenConstraintIsNotEmail() {
		final LockiUser lockiUser = new LockiUser(null, NAME, PHONE, EMAIL, null, null, null);
		final UserEntity entity = this.buildUserEntity();

		when(this.userMapper.toEntity(lockiUser)).thenReturn(entity);
		when(this.userRepository.saveAndFlush(entity)).thenThrow(
				new DataIntegrityViolationException("other constraint",
						new ConstraintViolationException("other constraint", new SQLException(), "other_constraint")));

		assertThatThrownBy(() -> this.userRepositoryAdapter.insert(lockiUser))
				.isInstanceOf(DataIntegrityViolationException.class)
				.hasMessageContaining("other constraint");
	}

	@Test
	void shouldRethrowDataIntegrityViolationExceptionWhenCauseIsNotConstraintViolation() {
		final LockiUser lockiUser = new LockiUser(null, NAME, PHONE, EMAIL, null, null, null);
		final UserEntity entity = this.buildUserEntity();

		when(this.userMapper.toEntity(lockiUser)).thenReturn(entity);
		when(this.userRepository.saveAndFlush(entity)).thenThrow(
				new DataIntegrityViolationException("not null violation", new RuntimeException("some cause")));

		assertThatThrownBy(() -> this.userRepositoryAdapter.insert(lockiUser))
				.isInstanceOf(DataIntegrityViolationException.class)
        .hasMessageContaining("not null violation");
	}

	private UserEntity buildUserEntity() {
		return UserEntity.builder()
				.id(UUID.randomUUID())
				.name(NAME)
				.email(EMAIL)
				.phone(PHONE)
				.version(0L)
				.createdAt(Instant.now())
				.updatedAt(Instant.now())
				.build();
	}
}