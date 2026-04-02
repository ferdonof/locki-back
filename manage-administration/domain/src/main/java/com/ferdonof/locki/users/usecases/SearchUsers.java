package com.ferdonof.locki.users.usecases;

import com.ferdonof.locki.users.entities.LockiUser;
import com.ferdonof.locki.users.entities.UserFilter;

import java.util.List;

public interface SearchUsers {
	List<LockiUser> execute(UserFilter filter);
}

