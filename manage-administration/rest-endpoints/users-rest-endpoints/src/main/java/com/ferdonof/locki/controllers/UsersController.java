package com.ferdonof.locki.controllers;

import com.ferdonof.locki.external.admin.dto.CreateUserRequestDTO;
import com.ferdonof.locki.mappers.UserDtoMapper;
import com.ferdonof.locki.users.entities.LockiUser;
import com.ferdonof.locki.users.usecases.CreateUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UsersController {

	private final UserDtoMapper userDtoMapper;

	private final CreateUser createUserUseCase;

	@PostMapping
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public LockiUser create(@RequestBody @Valid CreateUserRequestDTO request) {
		return this.createUserUseCase.execute(this.userDtoMapper.toDomain(request));
	}

}
