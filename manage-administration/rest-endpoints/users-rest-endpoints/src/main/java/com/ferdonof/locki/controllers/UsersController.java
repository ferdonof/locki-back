package com.ferdonof.locki.controllers;

import com.ferdonof.locki.users.entities.LockiUser;
import com.ferdonof.locki.users.usecases.CreateUser;
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

	private final CreateUser createUserUseCase;

	@PostMapping
	@ResponseBody
	@ResponseStatus(HttpStatus.CREATED)
	public LockiUser create(@RequestBody LockiUser lockiUser) {
		return this.createUserUseCase.execute(lockiUser);
	}

}
