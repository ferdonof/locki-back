package com.ferdonof.locki.reservations.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ferdonof.locki.reservations.entities.LocationEntity;

public interface LocationsRepository extends JpaRepository<LocationEntity, UUID> {
}
