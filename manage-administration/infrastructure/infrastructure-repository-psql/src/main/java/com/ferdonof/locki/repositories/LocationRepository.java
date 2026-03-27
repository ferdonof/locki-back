package com.ferdonof.locki.repositories;

import com.ferdonof.locki.entities.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LocationRepository extends JpaRepository<LocationEntity, UUID> {

}
