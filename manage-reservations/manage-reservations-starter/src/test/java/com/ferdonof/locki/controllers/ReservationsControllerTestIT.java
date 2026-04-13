package com.ferdonof.locki.controllers;

import static com.ferdonof.locki.lockers.enums.LockerSize.SMALL;
import static com.ferdonof.locki.lockers.enums.LockerStatus.AVAILABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferdonof.locki.entities.CachedFee;
import com.ferdonof.locki.fee.entities.Fee;
import com.ferdonof.locki.lockers.enums.RackStatus;
import com.ferdonof.locki.reservations.entities.LocationEntity;
import com.ferdonof.locki.reservations.entities.RackedLockerEntity;
import com.ferdonof.locki.reservations.entities.ReservationEntity;
import com.ferdonof.locki.reservations.enums.ReservationStatus;
import com.ferdonof.locki.reservations.repositories.LocationsRepository;
import com.ferdonof.locki.reservations.repositories.RackedLockersRepository;
import com.ferdonof.locki.reservations.repositories.ReservationsRepository;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ReservationsControllerTestIT {

  private static final String RESERVATIONS_URL = "/reservations";

  private static final Fee FEE = Fee
      .builder()
      .lockerSize(SMALL)
      .country("ARGENTINA")
      .currency("ARS")
      .price(BigDecimal.valueOf(23.5))
      .build();

  public static final String REDIS_FEE_KEY = "fee:%s:%s";

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

  @Container
  @ServiceConnection(name = "redis")
  static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
      .withExposedPorts(6379);

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private RackedLockersRepository rackedLockersRepository;

  @Autowired
  private ReservationsRepository reservationsRepository;

  @Autowired
  private LocationsRepository locationsRepository;

  @Autowired
  private StringRedisTemplate redisTemplate;

  @BeforeEach
  void setUp() {
    this.reservationsRepository.deleteAll();
    this.rackedLockersRepository.deleteAll();
    this.locationsRepository.deleteAll();
    this.redisTemplate
        .getConnectionFactory()
        .getConnection()
        .serverCommands()
        .flushAll();
  }

  @Test
  void create_whenRequestIsValid_thenReturnsCreated() throws Exception {
    final ClassPathResource content = new ClassPathResource("mocks/requests/create-reservation-request.json");
    final UUID lockerId = UUID.fromString("82b25906-2166-4cd0-8c6a-607d209d8b31");
    final UUID rackId = UUID.fromString("259f0b04-ca24-4bca-906a-10c813518e21");

    final LocationEntity location = this.locationsRepository.saveAndFlush(buildLocation());

    final RackedLockerEntity rackedLocker = buildRackedLocker(lockerId, rackId, location);

    final CachedFee fee = buildCachedFee();

    this.buildRedisKey(fee);

    this.rackedLockersRepository.saveAndFlush(rackedLocker);

    this.mockMvc
        .perform(MockMvcRequestBuilders
            .post(RESERVATIONS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content.getContentAsByteArray()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.lockerId").value("82b25906-2166-4cd0-8c6a-607d209d8b31"))
        .andExpect(jsonPath("$.country").value("ARGENTINA"))
        .andExpect(jsonPath("$.currency").value("ARS"))
        .andExpect(jsonPath("$.price").value(23.5))
        .andExpect(jsonPath("$.startDate").value("2024-06-01T10:00:00Z"))
        .andExpect(jsonPath("$.endDate").value("2024-06-01T12:00:00Z"));

    final Optional<ReservationEntity> entity = this.reservationsRepository
        .findByLockerIdAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndStatusIs(
            UUID.fromString("82b25906-2166-4cd0-8c6a-607d209d8b31"),
            Instant.parse("2024-06-01T10:00:00Z"),
            Instant.parse("2024-06-01T12:00:00Z"),
            ReservationStatus.ACTIVE);

    assertTrue(entity.isPresent());
  }

  @Test
  void create_whenRequestIsValidAndSlotIsUnavailable_thenReturnConflict() throws Exception {
    final ClassPathResource content = new ClassPathResource("mocks/requests/create-reservation-request.json");
    final UUID lockerId = UUID.fromString("82b25906-2166-4cd0-8c6a-607d209d8b31");
    final UUID rackId = UUID.fromString("259f0b04-ca24-4bca-906a-10c813518e21");
    final LocationEntity location = this.locationsRepository.saveAndFlush(buildLocation());

    final RackedLockerEntity rackedLocker = buildRackedLocker(lockerId, rackId, location);

    final CachedFee fee = buildCachedFee();

    this.buildRedisKey(fee);

    final RackedLockerEntity rackedLockerEntity = this.rackedLockersRepository.saveAndFlush(rackedLocker);
    this.reservationsRepository.saveAndFlush(buildReservationEntity(rackedLockerEntity, fee, location));

    this.mockMvc
        .perform(MockMvcRequestBuilders
            .post(RESERVATIONS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content.getContentAsByteArray()))
        .andExpect(status().isConflict());

    assertThat(this.reservationsRepository.count()).isOne();
  }

  @Test
  void create_whenRequestIsValidAndFeeIsNotCached_thenReturnNotFound() throws Exception {
    final ClassPathResource content = new ClassPathResource("mocks/requests/create-reservation-request.json");
    final UUID lockerId = UUID.fromString("82b25906-2166-4cd0-8c6a-607d209d8b31");
    final UUID rackId = UUID.fromString("259f0b04-ca24-4bca-906a-10c813518e21");

    final LocationEntity location = this.locationsRepository.saveAndFlush(buildLocation());
    final RackedLockerEntity rackedLocker = buildRackedLocker(lockerId, rackId, location);

    this.rackedLockersRepository.saveAndFlush(rackedLocker);

    this.mockMvc
        .perform(MockMvcRequestBuilders
            .post(RESERVATIONS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content.getContentAsByteArray()))
        .andExpect(status().isNotFound());

    assertThat(this.reservationsRepository.count()).isZero();
  }

  @Test
  void create_whenRequestIsValidRackedLockerNotExists_thenReturnNotFound() throws Exception {
    final ClassPathResource content = new ClassPathResource("mocks/requests/create-reservation-request.json");

    final CachedFee fee = buildCachedFee();

    this.buildRedisKey(fee);

    this.mockMvc
        .perform(MockMvcRequestBuilders
            .post(RESERVATIONS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content.getContentAsByteArray()))
        .andExpect(status().isNotFound());

    assertThat(this.reservationsRepository.count()).isZero();
  }

  @Test
  void create_withRackOnlyAndSlotAvailable_thenReturnCreated() throws Exception {
    final ClassPathResource content = new ClassPathResource("mocks/requests/create-reservation-request-only-rackId.json");
    final UUID rackId = UUID.fromString("4cff63a5-74c9-4b0a-8ee1-3c9cad8a97bb");
    final LocationEntity location = this.locationsRepository.saveAndFlush(buildLocation());

    final RackedLockerEntity rackedLocker1 = buildRackedLocker(UUID.randomUUID(), rackId, location);
    final RackedLockerEntity rackedLocker2 = buildRackedLocker(UUID.randomUUID(), rackId, location);
    final RackedLockerEntity rackedLocker3 = buildRackedLocker(UUID.randomUUID(), rackId, location);

    final CachedFee fee = buildCachedFee();

    this.buildRedisKey(fee);

    this.rackedLockersRepository.saveAndFlush(rackedLocker1);
    this.rackedLockersRepository.saveAndFlush(rackedLocker2);
    this.rackedLockersRepository.saveAndFlush(rackedLocker3);

    this.mockMvc
        .perform(MockMvcRequestBuilders
            .post(RESERVATIONS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content.getContentAsByteArray()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.lockerId").isNotEmpty())
        .andExpect(jsonPath("$.country").value("ARGENTINA"))
        .andExpect(jsonPath("$.currency").value("ARS"))
        .andExpect(jsonPath("$.price").value(23.5))
        .andExpect(jsonPath("$.startDate").value("2024-06-01T10:00:00Z"))
        .andExpect(jsonPath("$.endDate").value("2024-06-01T12:00:00Z"));

    assertThat(this.rackedLockersRepository.count()).isEqualTo(3);
    assertThat(this.reservationsRepository.count()).isOne();
    assertThat(this.reservationsRepository
        .findAll()
        .getFirst()
        .getRackId()).isEqualTo(rackId);
  }

  @Test
  void execute_withRackOnlyAndLockerNotExists_thenReturnNotFound() throws Exception {
    final ClassPathResource content = new ClassPathResource("mocks/requests/create-reservation-request-only-rackId.json");

    final CachedFee fee = buildCachedFee();

    this.buildRedisKey(fee);

    this.mockMvc
        .perform(MockMvcRequestBuilders
            .post(RESERVATIONS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content.getContentAsByteArray()))
        .andExpect(status().isNotFound());

    assertThat(this.reservationsRepository.count()).isZero();
  }

  private static CachedFee buildCachedFee() {
    return CachedFee
        .builder()
        .lockerSize("SMALL")
        .country("ARGENTINA")
        .currency("ARS")
        .price(BigDecimal.valueOf(23.5))
        .build();
  }

  private static RackedLockerEntity buildRackedLocker(UUID lockerId, UUID rackId, LocationEntity location) {
    return RackedLockerEntity
        .builder()
        .lockerId(lockerId)
        .rackId(rackId)
        .position(1)
        .rackStatus(RackStatus.ACTIVE)
        .size(SMALL)
        .lockerStatus(AVAILABLE)
        .location(location)
        .build();
  }

  public static ReservationEntity buildReservationEntity(RackedLockerEntity locker, CachedFee fee, LocationEntity location) {
    return ReservationEntity
        .builder()
        .lockerId(locker.getLockerId())
        .rackId(locker.getRackId())
        .position(locker.getPosition())
        .location(location)
        .status(ReservationStatus.ACTIVE)
        .startDate(Instant.parse("2024-06-01T10:00:00Z"))
        .endDate(Instant.parse("2024-06-01T12:00:00Z"))
        .userId(UUID.randomUUID())
        .price(fee.price())
        .currency(fee.currency())
        .build();
  }

  private void buildRedisKey(CachedFee fee) throws Exception {
    this.redisTemplate
        .opsForValue()
        .set(String.format(REDIS_FEE_KEY, fee.country(), fee.lockerSize()), this.objectMapper.writeValueAsString(fee));
  }

  private static LocationEntity buildLocation() {
    return LocationEntity
        .builder()
        .locationId(UUID.randomUUID())
        .address("Cabildo # 123")
        .city("Buenos Aires")
        .country("ARGENTINA")
        .zipCode("1000")
        .lat(BigDecimal.ONE)
        .lon(BigDecimal.ONE)
        .build();
  }
}


