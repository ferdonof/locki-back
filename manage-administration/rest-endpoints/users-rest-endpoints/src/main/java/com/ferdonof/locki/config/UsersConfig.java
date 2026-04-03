package com.ferdonof.locki.config;

import com.ferdonof.locki.users.ports.UserRepositoryPort;
import com.ferdonof.locki.users.usecases.CreateUser;
import com.ferdonof.locki.users.usecases.CreateUserImpl;
import com.ferdonof.locki.users.usecases.GetUser;
import com.ferdonof.locki.users.usecases.GetUserImpl;
import com.ferdonof.locki.users.usecases.SearchUsers;
import com.ferdonof.locki.users.usecases.SearchUsersImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class UsersConfig {

	@Bean
	public CreateUser createUserUseCase(TransactionTemplate transactionTemplate, UserRepositoryPort userRepository) {
		return new CreateUserImpl(transactionTemplate, userRepository);
	}

	@Bean
	public GetUser getUserUseCase(UserRepositoryPort userRepository) {
		return new GetUserImpl(userRepository);
	}

	@Bean
	public SearchUsers searchUsersUseCase(UserRepositoryPort userRepository) {
		return new SearchUsersImpl(userRepository);
	}
}
