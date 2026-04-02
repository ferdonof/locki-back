package com.ferdonof.locki.repositories;

import com.ferdonof.locki.entities.LockerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface LockerRepository extends JpaRepository<LockerEntity, UUID>, JpaSpecificationExecutor<LockerEntity> {

  List<LockerEntity> findByRackId(UUID rackId);
}
