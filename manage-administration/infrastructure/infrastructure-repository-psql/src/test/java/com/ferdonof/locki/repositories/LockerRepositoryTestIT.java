package com.ferdonof.locki.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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
import com.ferdonof.locki.entities.LockerEntity;
import com.ferdonof.locki.entities.RackEntity;
import com.ferdonof.locki.lockers.enums.LatchStatus;
import com.ferdonof.locki.lockers.enums.LockerStatus;
import com.ferdonof.locki.racks.enums.RackStatus;

@Tag("integration")
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LockerRepositoryTestIT {

  @Autowired
  private LockerRepository lockerRepository;

  @Autowired
  private RackRepository rackRepository;

  @Autowired
  private LocationRepository locationRepository;

  @Autowired
  private TestEntityManager entityManager;

  private RackEntity testRack;

  private LocationEntity testLocation;

  @BeforeEach
  void setUp() {
    this.lockerRepository.deleteAll();
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

    this.testRack = RackEntity
        .builder()
        .serial(1)
        .size(10)
        .status(RackStatus.ACTIVE)
        .location(this.testLocation)
        .build();
    this.rackRepository.save(this.testRack);
  }

  @Test
  void whenSaveLocker_thenSaveAndFindLockerById() {
    final LockerEntity locker = LockerEntity
        .builder()
        .serial(1)
        .rack(this.testRack)
        .status(LockerStatus.AVAILABLE)
        .latchStatus(LatchStatus.LOCKED)
        .build();

    final LockerEntity saved = this.lockerRepository.save(locker);
    this.entityManager.flush();

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getCreatedAt()).isNotNull();
    assertThat(saved.getUpdatedAt()).isNotNull();
    assertThat(saved.getVersion()).isNotNull();

    final Optional<LockerEntity> found = this.lockerRepository.findById(saved.getId());

    assertThat(found).isPresent();
    assertThat(found
        .get()
        .getSerial()).isEqualTo(1);
    assertThat(found
        .get()
        .getStatus()).isEqualTo(LockerStatus.AVAILABLE);
    assertThat(found
        .get()
        .getLatchStatus()).isEqualTo(LatchStatus.LOCKED);
    assertThat(found
        .get()
        .getRack()
        .getId()).isEqualTo(this.testRack.getId());
  }

  @Test
  void whenLockerNotFound_thenReturnEmpty() {
    final Optional<LockerEntity> found = this.lockerRepository.findById(UUID.randomUUID());

    assertThat(found).isEmpty();
  }

  @Test
  void shouldDeleteLocker() {
    final LockerEntity locker = LockerEntity
        .builder()
        .serial(2)
        .rack(this.testRack)
        .status(LockerStatus.RESERVED)
        .latchStatus(LatchStatus.UNLOCKED)
        .build();

    final LockerEntity saved = this.lockerRepository.save(locker);

    this.lockerRepository.deleteById(saved.getId());

    assertThat(this.lockerRepository.findById(saved.getId())).isEmpty();
  }

  @Test
  void whenLockerSaved_thenFindAllLockers() {
    final LockerEntity locker1 = LockerEntity
        .builder()
        .serial(1)
        .rack(this.testRack)
        .status(LockerStatus.AVAILABLE)
        .latchStatus(LatchStatus.LOCKED)
        .build();

    final LockerEntity locker2 = LockerEntity
        .builder()
        .serial(2)
        .rack(this.testRack)
        .status(LockerStatus.RESERVED)
        .latchStatus(LatchStatus.UNLOCKED)
        .build();

    this.lockerRepository.save(locker1);
    this.lockerRepository.save(locker2);

    final Iterable<LockerEntity> lockers = this.lockerRepository.findAll();

    assertThat(lockers).hasSize(2);
  }

  @Test
  void shouldCountLockers() {
    final LockerEntity locker = LockerEntity
        .builder()
        .serial(1)
        .rack(this.testRack)
        .status(LockerStatus.AVAILABLE)
        .latchStatus(LatchStatus.LOCKED)
        .build();

    this.lockerRepository.save(locker);

    assertThat(this.lockerRepository.count()).isEqualTo(1);
  }

  @Test
  void whenFindByRackId_thenReturnLockersForRack() {
    final RackEntity anotherRack = RackEntity
        .builder()
        .serial(2)
        .size(10)
        .status(RackStatus.ACTIVE)
        .location(this.testLocation)
        .build();
    this.rackRepository.save(anotherRack);

    final LockerEntity locker1 = LockerEntity
        .builder()
        .serial(1)
        .rack(this.testRack)
        .status(LockerStatus.AVAILABLE)
        .latchStatus(LatchStatus.LOCKED)
        .build();

    final LockerEntity locker2 = LockerEntity
        .builder()
        .serial(2)
        .rack(this.testRack)
        .status(LockerStatus.RESERVED)
        .latchStatus(LatchStatus.LOCKED)
        .build();

    final LockerEntity locker3 = LockerEntity
        .builder()
        .serial(1)
        .rack(anotherRack)
        .status(LockerStatus.AVAILABLE)
        .latchStatus(LatchStatus.LOCKED)
        .build();

    this.lockerRepository.save(locker1);
    this.lockerRepository.save(locker2);
    this.lockerRepository.save(locker3);

    final List<LockerEntity> foundLockers = this.lockerRepository.findByRackId(this.testRack.getId());

    assertThat(foundLockers).hasSize(2);
    assertThat(foundLockers).allMatch(locker -> locker
        .getRack()
        .getId()
        .equals(this.testRack.getId()));
  }

  @Test
  void whenFindByRackIdWithNoLockers_thenReturnEmptyList() {
    final List<LockerEntity> foundLockers = this.lockerRepository.findByRackId(this.testRack.getId());

    assertThat(foundLockers).isEmpty();
  }

  @Test
  void whenUpdateLocker_thenPersistChanges() {
    final LockerEntity locker = LockerEntity
        .builder()
        .serial(1)
        .rack(this.testRack)
        .status(LockerStatus.AVAILABLE)
        .latchStatus(LatchStatus.LOCKED)
        .build();

    final LockerEntity saved = this.lockerRepository.save(locker);
    this.entityManager.flush();

    final LockerEntity toUpdate = this.lockerRepository
        .findById(saved.getId())
        .orElseThrow();
    final LockerEntity updated = this.lockerRepository.save(
        LockerEntity
            .builder()
            .id(toUpdate.getId())
            .serial(toUpdate.getSerial())
            .rack(toUpdate.getRack())
            .status(LockerStatus.RESERVED)
            .latchStatus(LatchStatus.UNLOCKED)
            .version(toUpdate.getVersion())
            .createdAt(toUpdate.getCreatedAt())
            .updatedAt(toUpdate.getUpdatedAt())
            .build()
    );
    this.entityManager.flush();

    final Optional<LockerEntity> found = this.lockerRepository.findById(updated.getId());

    assertThat(found).isPresent();
    assertThat(found
        .get()
        .getStatus()).isEqualTo(LockerStatus.RESERVED);
    assertThat(found
        .get()
        .getLatchStatus()).isEqualTo(LatchStatus.UNLOCKED);
  }

  @Test
  void whenLockerHasDifferentStatuses_thenPersistCorrectly() {
    final LockerEntity availableLocker = LockerEntity
        .builder()
        .serial(1)
        .rack(this.testRack)
        .status(LockerStatus.AVAILABLE)
        .latchStatus(LatchStatus.LOCKED)
        .build();

    final LockerEntity reservedLocker = LockerEntity
        .builder()
        .serial(2)
        .rack(this.testRack)
        .status(LockerStatus.RESERVED)
        .latchStatus(LatchStatus.UNLOCKED)
        .build();

    final LockerEntity occupiedLocker = LockerEntity
        .builder()
        .serial(3)
        .rack(this.testRack)
        .status(LockerStatus.OCCUPIED)
        .latchStatus(LatchStatus.LOCKED)
        .build();

    this.lockerRepository.save(availableLocker);
    this.lockerRepository.save(reservedLocker);
    this.lockerRepository.save(occupiedLocker);

    final Iterable<LockerEntity> allLockers = this.lockerRepository.findAll();

    assertThat(allLockers).hasSize(3);
    assertThat(allLockers).anySatisfy(locker -> assertThat(locker.getStatus()).isEqualTo(LockerStatus.AVAILABLE));
    assertThat(allLockers).anySatisfy(locker -> assertThat(locker.getStatus()).isEqualTo(LockerStatus.RESERVED));
    assertThat(allLockers).anySatisfy(locker -> assertThat(locker.getStatus()).isEqualTo(LockerStatus.OCCUPIED));
  }
}

