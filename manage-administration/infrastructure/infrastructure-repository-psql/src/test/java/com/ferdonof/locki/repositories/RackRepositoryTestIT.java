package com.ferdonof.locki.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.ferdonof.locki.entities.LocationEntity;
import com.ferdonof.locki.entities.RackEntity;
import com.ferdonof.locki.racks.enums.RackStatus;

@Tag("integration")
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RackRepositoryTestIT {

  @Autowired
  private RackRepository rackRepository;

  @Autowired
  private LocationRepository locationRepository;

  @Autowired
  private TestEntityManager entityManager;

  private LocationEntity testLocation;

  @BeforeEach
  void setUp() {
    this.rackRepository.deleteAll();
    this.locationRepository.deleteAll();

    this.testLocation = LocationEntity
        .builder()
        .address("123 Main St")
        .code("LOC001")
        .city("New York")
        .country("USA")
        .build();
    this.locationRepository.save(this.testLocation);
  }

  @Test
  void whenSaveRack_thenSaveAndFindRackById() {
    final RackEntity rack = RackEntity
        .builder()
        .serial(1)
        .size(10)
        .status(RackStatus.ACTIVE)
        .location(this.testLocation)
        .build();

    final RackEntity saved = this.rackRepository.save(rack);
    this.entityManager.flush();

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getCreatedAt()).isNotNull();
    assertThat(saved.getUpdatedAt()).isNotNull();
    assertThat(saved.getVersion()).isNotNull();

    final Optional<RackEntity> found = this.rackRepository.findById(saved.getId());

    assertThat(found).isPresent();
    assertThat(found
        .get()
        .getSerial()).isEqualTo(1);
    assertThat(found
        .get()
        .getSize()).isEqualTo(10);
    assertThat(found
        .get()
        .getStatus()).isEqualTo(RackStatus.ACTIVE);
    assertThat(found
        .get()
        .getLocation()
        .getId()).isEqualTo(this.testLocation.getId());
  }

  @Test
  void whenRackNotFound_thenReturnEmpty() {
    final Optional<RackEntity> found = this.rackRepository.findById(UUID.randomUUID());

    assertThat(found).isEmpty();
  }

  @Test
  void shouldDeleteRack() {
    final RackEntity rack = RackEntity
        .builder()
        .serial(1)
        .size(15)
        .status(RackStatus.MAINTENANCE)
        .location(this.testLocation)
        .build();

    final RackEntity saved = this.rackRepository.save(rack);

    this.rackRepository.deleteById(saved.getId());

    assertThat(this.rackRepository.findById(saved.getId())).isEmpty();
  }

  @Test
  void whenRackSaved_thenFindAllRacks() {
    final RackEntity rack1 = RackEntity
        .builder()
        .serial(1)
        .size(10)
        .status(RackStatus.ACTIVE)
        .location(this.testLocation)
        .build();

    final RackEntity rack2 = RackEntity
        .builder()
        .serial(2)
        .size(20)
        .status(RackStatus.ACTIVE)
        .location(this.testLocation)
        .build();

    this.rackRepository.save(rack1);
    this.rackRepository.save(rack2);

    final Iterable<RackEntity> racks = this.rackRepository.findAll();

    assertThat(racks).hasSize(2);
  }

  @Test
  void shouldCountRacks() {
    final RackEntity rack = RackEntity
        .builder()
        .serial(1)
        .size(10)
        .status(RackStatus.ACTIVE)
        .location(this.testLocation)
        .build();

    this.rackRepository.save(rack);

    assertThat(this.rackRepository.count()).isEqualTo(1);
  }

  @Test
  void whenUpdateRack_thenPersistChanges() {
    final RackEntity rack = RackEntity
        .builder()
        .serial(1)
        .size(10)
        .status(RackStatus.ACTIVE)
        .location(this.testLocation)
        .build();

    final RackEntity saved = this.rackRepository.save(rack);
    this.entityManager.flush();

    final RackEntity toUpdate = this.rackRepository
        .findById(saved.getId())
        .orElseThrow();
    final RackEntity updated = this.rackRepository.save(
        RackEntity
            .builder()
            .id(toUpdate.getId())
            .serial(toUpdate.getSerial())
            .size(toUpdate.getSize())
            .status(RackStatus.MAINTENANCE)
            .location(toUpdate.getLocation())
            .lockers(toUpdate.getLockers())
            .version(toUpdate.getVersion())
            .createdAt(toUpdate.getCreatedAt())
            .updatedAt(toUpdate.getUpdatedAt())
            .build()
    );
    this.entityManager.flush();

    final Optional<RackEntity> found = this.rackRepository.findById(updated.getId());

    assertThat(found).isPresent();
    assertThat(found
        .get()
        .getStatus()).isEqualTo(RackStatus.MAINTENANCE);
  }

  @Test
  void whenSaveMultipleRacks_thenVerifyVersionHandling() {
    final RackEntity rack1 = RackEntity
        .builder()
        .serial(1)
        .size(10)
        .status(RackStatus.ACTIVE)
        .location(this.testLocation)
        .build();

    final RackEntity rack2 = RackEntity
        .builder()
        .serial(2)
        .size(15)
        .status(RackStatus.INACTIVE)
        .location(this.testLocation)
        .build();

    final RackEntity saved1 = this.rackRepository.save(rack1);
    final RackEntity saved2 = this.rackRepository.save(rack2);

    assertThat(saved1.getVersion()).isNotNull();
    assertThat(saved2.getVersion()).isNotNull();

    final Optional<RackEntity> found1 = this.rackRepository.findById(saved1.getId());
    final Optional<RackEntity> found2 = this.rackRepository.findById(saved2.getId());

    assertThat(found1
        .get()
        .getVersion()).isEqualTo(saved1.getVersion());
    assertThat(found2
        .get()
        .getVersion()).isEqualTo(saved2.getVersion());
  }

  @Test
  void whenRackHasDifferentStatuses_thenPersistCorrectly() {
    final RackEntity activeRack = RackEntity
        .builder()
        .serial(1)
        .size(10)
        .status(RackStatus.ACTIVE)
        .location(this.testLocation)
        .build();

    final RackEntity maintenanceRack = RackEntity
        .builder()
        .serial(2)
        .size(10)
        .status(RackStatus.MAINTENANCE)
        .location(this.testLocation)
        .build();

    final RackEntity inactiveRack = RackEntity
        .builder()
        .serial(3)
        .size(10)
        .status(RackStatus.INACTIVE)
        .location(this.testLocation)
        .build();

    this.rackRepository.save(activeRack);
    this.rackRepository.save(maintenanceRack);
    this.rackRepository.save(inactiveRack);

    final Iterable<RackEntity> allRacks = this.rackRepository.findAll();

    assertThat(allRacks).hasSize(3);
    assertThat(allRacks).anySatisfy(rack -> assertThat(rack.getStatus()).isEqualTo(RackStatus.ACTIVE));
    assertThat(allRacks).anySatisfy(rack -> assertThat(rack.getStatus()).isEqualTo(RackStatus.MAINTENANCE));
    assertThat(allRacks).anySatisfy(rack -> assertThat(rack.getStatus()).isEqualTo(RackStatus.INACTIVE));
  }
}

