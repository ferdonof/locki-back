package com.ferdonof.locki.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ferdonof.locki.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
}
