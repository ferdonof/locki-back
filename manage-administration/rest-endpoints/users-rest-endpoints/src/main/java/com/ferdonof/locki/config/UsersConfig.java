package com.ferdonof.locki.config;

import com.ferdonof.locki.users.ports.UserRepositoryPort;
import com.ferdonof.locki.users.usecases.CreateUser;
import com.ferdonof.locki.users.usecases.CreateUserImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsersConfig {

	@Bean
	public CreateUser createUserUseCase(UserRepositoryPort userRepository) {
		return new CreateUserImpl(userRepository);
	}
}
