package com.ferdonof.locki.users.usecases;

import com.ferdonof.locki.users.entities.LockiUser;
import com.ferdonof.locki.users.entities.UserFilter;
import com.ferdonof.locki.users.ports.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class SearchUsersImpl implements SearchUsers {

	private final UserRepositoryPort userRepository;

	@Override
	public List<LockiUser> execute(UserFilter filter) {
		log.info("Searching users with limit {} and offset {}", filter.limit(), filter.offset());
		return this.userRepository.search(filter);
	}
}

