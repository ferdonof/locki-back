package com.ferdonof.locki.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.ferdonof.locki.entities.UserEntity;
import com.ferdonof.locki.users.entities.LockiUser;

class LockiUserMapperTest {

	private final LockiUserMapper lockiUserMapper = Mappers.getMapper(LockiUserMapper.class);

	@Test
	void withUserEntity_thenMapToDomain() {
		final UserEntity userEntity = UserEntity.builder()
																			.id(UUID.randomUUID())
																			.name("John Doe")
																			.email("john@email.com")
																			.phone("+123456789")
																			.createdAt(Instant.now())
																			.updatedAt(Instant.now())
																			.version(1L)
																			.build();

		final var result = this.lockiUserMapper.toDomain(userEntity);
		assertThat(result.id()).isEqualTo(userEntity.getId());
		assertThat(result.name()).isEqualTo(userEntity.getName());
		assertThat(result.email()).isEqualTo(userEntity.getEmail());
		assertThat(result.phone()).isEqualTo(userEntity.getPhone());
		assertThat(result.version()).isEqualTo(userEntity.getVersion());
		assertThat(result.createdAt()).isEqualTo(userEntity.getCreatedAt());
		assertThat(result.updatedAt()).isEqualTo(userEntity.getUpdatedAt());

	}

	@Test
	void withNullUserEntity_thenMapToNullDomain() {
		final var result = this.lockiUserMapper.toDomain(null);
		assertThat(result).isNull();
	}

	@Test
	void withLockiUser_thenMapToEntity() {
		final Instant now = Instant.now();
		final var lockiUser = LockiUser.builder()
																	 .id(UUID.randomUUID())
																	 .name("John Doe")
																	 .email("john@email.com")
																	 .phone("+123456789")
																	 .createdAt(now)
																	 .updatedAt(now)
																	 .version(1L)
																	 .build();

		final var result = this.lockiUserMapper.toEntity(lockiUser);

		assertThat(result.getId()).isEqualTo(lockiUser.id());
		assertThat(result.getName()).isEqualTo(lockiUser.name());
		assertThat(result.getEmail()).isEqualTo(lockiUser.email());
		assertThat(result.getPhone()).isEqualTo(lockiUser.phone());
		assertThat(result.getVersion()).isEqualTo(lockiUser.version());
		assertThat(result.getCreatedAt()).isEqualTo(lockiUser.createdAt());
		assertThat(result.getUpdatedAt()).isEqualTo(lockiUser.updatedAt());
	}

	@Test
	void withNullLockiUser_thenMapToNullEntity() {
		final var result = this.lockiUserMapper.toEntity(null);
		assertThat(result).isNull();
	}
}