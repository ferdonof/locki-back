package com.ferdonof.locki.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import com.ferdonof.locki.entities.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, UUID> {
}
