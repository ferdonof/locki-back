package com.ferdonof.locki.repositories;

import com.ferdonof.locki.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
}
