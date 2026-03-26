package com.ferdonof.locki.adapters;

import com.ferdonof.locki.entities.UserEntity;
import com.ferdonof.locki.mappers.LockiUserMapper;
import com.ferdonof.locki.repositories.UserRepository;
import com.ferdonof.locki.users.entities.LockiUser;
import com.ferdonof.locki.commons.exceptions.GenericClientException;
import com.ferdonof.locki.users.exceptions.UserAlreadyExistsException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

	public static final String NAME = "John Doe";

	public static final String PHONE = "+123456789";

	public static final String EMAIL = "john@example.com";

	@Mock
	private UserRepository userRepository;

	@Mock
	private LockiUserMapper lockiUserMapper;

	@InjectMocks
	private UserRepositoryAdapter userRepositoryAdapter;

	@Test
	void shouldInsertUserSuccessfully() {
		final LockiUser lockiUser = new LockiUser(null, NAME, PHONE, EMAIL, null, null, null);
		final UserEntity entity = this.buildUserEntity();
		final LockiUser expectedResult = new LockiUser(entity.getId(), NAME, PHONE, EMAIL, entity.getVersion(),
				entity.getCreatedAt(), entity.getUpdatedAt());

		when(this.lockiUserMapper.toEntity(lockiUser)).thenReturn(entity);
		when(this.userRepository.saveAndFlush(entity)).thenReturn(entity);
		when(this.lockiUserMapper.toDomain(entity)).thenReturn(expectedResult);

		final LockiUser result = this.userRepositoryAdapter.insert(lockiUser);

		assertThat(result).isEqualTo(expectedResult);
		assertThat(result.id()).isNotNull();
		assertThat(result.email()).isEqualTo(EMAIL);
		verify(this.lockiUserMapper).toEntity(lockiUser);
		verify(this.userRepository).saveAndFlush(entity);
		verify(this.lockiUserMapper).toDomain(entity);
	}

	@Test
	void shouldThrowUserAlreadyExistsExceptionWhenEmailIsDuplicated() {
		final LockiUser lockiUser = new LockiUser(null, NAME, PHONE, EMAIL, null, null, null);
		final UserEntity entity = this.buildUserEntity();

		when(this.lockiUserMapper.toEntity(lockiUser)).thenReturn(entity);
		when(this.userRepository.saveAndFlush(entity)).thenThrow(new DataIntegrityViolationException("duplicate key",
				new ConstraintViolationException("duplicate key", new SQLException(), "uk_users_email")));

		assertThatThrownBy(() -> this.userRepositoryAdapter.insert(lockiUser))
				.isInstanceOf(UserAlreadyExistsException.class).hasMessageContaining(EMAIL);
	}

	@Test
	void shouldThrowGenericClientExceptionWhenConstraintIsNotEmail() {
		final LockiUser lockiUser = new LockiUser(null, NAME, PHONE, EMAIL, null, null, null);
		final UserEntity entity = this.buildUserEntity();

		when(this.lockiUserMapper.toEntity(lockiUser)).thenReturn(entity);
		when(this.userRepository.saveAndFlush(entity)).thenThrow(new DataIntegrityViolationException("other constraint",
				new ConstraintViolationException("other constraint", new SQLException(), "other_constraint")));

		assertThatThrownBy(() -> this.userRepositoryAdapter.insert(lockiUser))
				.isInstanceOf(GenericClientException.class);
	}

	@Test
	void shouldThrowGenericClientExceptionWhenCauseIsNotConstraintViolation() {
		final LockiUser lockiUser = new LockiUser(null, NAME, PHONE, EMAIL, null, null, null);
		final UserEntity entity = this.buildUserEntity();

		when(this.lockiUserMapper.toEntity(lockiUser)).thenReturn(entity);
		when(this.userRepository.saveAndFlush(entity)).thenThrow(
				new DataIntegrityViolationException("not null violation", new RuntimeException("some cause")));

		assertThatThrownBy(() -> this.userRepositoryAdapter.insert(lockiUser))
				.isInstanceOf(GenericClientException.class);
	}

	private UserEntity buildUserEntity() {
		return UserEntity.builder().id(UUID.randomUUID()).name(NAME).email(EMAIL).phone(PHONE).version(0L)
				.createdAt(Instant.now()).updatedAt(Instant.now()).build();
	}
}