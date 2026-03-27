package com.ferdonof.locki.repositories;

import com.ferdonof.locki.entities.RackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RackRepository extends JpaRepository<RackEntity, UUID> {
}
