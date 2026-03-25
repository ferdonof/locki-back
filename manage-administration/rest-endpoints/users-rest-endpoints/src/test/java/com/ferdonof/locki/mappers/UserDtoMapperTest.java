package com.ferdonof.locki.mappers;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.ferdonof.locki.external.admin.dto.CreateUserRequestDTO;
import com.ferdonof.locki.users.entities.LockiUser;

class UserDtoMapperTest {

  private final UserDtoMapper userDtoMapper = Mappers.getMapper(UserDtoMapper.class);


  @Test
  void withCreateUserRequestDTO_thenMapToDomain() {
    final CreateUserRequestDTO createUserRequestDTO = new CreateUserRequestDTO();
    createUserRequestDTO.setName("John Doe");
    createUserRequestDTO.setEmail("john@email.com");
    createUserRequestDTO.setPhone("+123456789");

    final var result = this.userDtoMapper.toDomain(createUserRequestDTO);

    assertThat(result.name()).isEqualTo(createUserRequestDTO.getName());
    assertThat(result.email()).isEqualTo(createUserRequestDTO.getEmail());
    assertThat(result.phone()).isEqualTo(createUserRequestDTO.getPhone());

  }

  @Test
  void withNullCreateUserRequestDTO_thenMapToNullDomain() {
    final var result = this.userDtoMapper.toDomain(null);
    assertThat(result).isNull();
  }

  @Test
  void withLockiUser_thenMapToUserResponseDTO() {
    final UUID id = UUID.randomUUID();
    final Instant now = Instant.now();
    final var lockiUser = LockiUser
        .builder()
        .id(id)
        .name("John Doe")
        .email("john@email.com")
        .phone("+123456789")
        .createdAt(now)
        .updatedAt(now)
        .version(1L)
        .build();

    final var result = this.userDtoMapper.toDto(lockiUser);

    assertThat(result.getId()).isEqualTo(lockiUser.id());
    assertThat(result.getName()).isEqualTo(lockiUser.name());
    assertThat(result.getEmail()).isEqualTo(lockiUser.email());
    assertThat(result.getPhone()).isEqualTo(lockiUser.phone());

  }

  @Test
  void withNullLockiUser_thenMapToNullUserResponseDTO() {
    final var result = this.userDtoMapper.toDto(null);
    assertThat(result).isNull();
  }
}