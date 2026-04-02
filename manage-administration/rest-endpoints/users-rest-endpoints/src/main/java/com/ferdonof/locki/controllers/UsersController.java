package com.ferdonof.locki.controllers;

import com.ferdonof.locki.external.admin.dto.CreateUserRequestDTO;
import com.ferdonof.locki.external.admin.dto.UserFilterRequestDTO;
import com.ferdonof.locki.external.admin.dto.UserResponseDTO;
import com.ferdonof.locki.mappers.UserDtoMapper;
import com.ferdonof.locki.users.usecases.CreateUser;
import com.ferdonof.locki.users.usecases.GetUser;
import com.ferdonof.locki.users.usecases.SearchUsers;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UsersController {

	private final UserDtoMapper userDtoMapper;

	private final CreateUser createUserUseCase;

	private final GetUser getUserUseCase;

	private final SearchUsers searchUsersUseCase;

	@PostMapping
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponseDTO create(@RequestBody @Valid CreateUserRequestDTO request) {
		return this.userDtoMapper.toDto(this.createUserUseCase.execute(this.userDtoMapper.toDomain(request)));
	}

	@GetMapping("/{id}")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public UserResponseDTO getById(@PathVariable UUID id) {
		return this.userDtoMapper.toDto(this.getUserUseCase.execute(id));
	}

	@PostMapping("/search")
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public List<UserResponseDTO> search(@RequestBody @Valid UserFilterRequestDTO request) {
		return this.searchUsersUseCase.execute(this.userDtoMapper.toFilter(request))
				.stream()
				.map(this.userDtoMapper::toDto)
				.toList();
	}
}
