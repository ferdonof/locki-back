package com.ferdonof.locki.controllers;

//import static com.ferdonof.locki.lockers.enums.LockerSize.SMALL;
//import static com.ferdonof.locki.lockers.enums.LockerStatus.AVAILABLE;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.ferdonof.locki.reservations.repositories.RackedLockersRepository;
import com.ferdonof.locki.reservations.repositories.ReservationsRepository;

//import org.junit.jupiter.api.Test;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import com.ferdonof.locki.lockers.enums.RackStatus;
//import com.ferdonof.locki.reservations.entities.RackedLockerEntity;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ReservationsControllerTestIT {

  private static final String RESERVATIONS_URL = "/reservations";

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

  @Container
  @ServiceConnection(name = "redis")
  static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
      .withExposedPorts(6379);

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private RackedLockersRepository rackedLockersRepository;

  @Autowired
  private ReservationsRepository reservationsRepository;

  @Autowired
  private StringRedisTemplate redisTemplate;

  @BeforeEach
  void setUp() {
    this.reservationsRepository.deleteAll();
    this.redisTemplate.execute((RedisCallback<Void>) connection -> {
      connection
          .serverCommands()
          .flushAll();
      return null;
    });
  }

  //  @Test
  //  void create_whenRequestIsValid_thenReturnsCreated() throws Exception {
  //    final ClassPathResource content = new ClassPathResource("mocks.requests/create-reservation-request.json");
  //    final RackedLockerEntity rackedLocker = RackedLockerEntity
  //        .builder()
  //        .lockerId(UUID.fromString("82b25906-2166-4cd0-8c6a-607d209d8b31"))
  //        .rackId(UUID.randomUUID())
  //        .position(1)
  //        .rackStatus(RackStatus.ACTIVE)
  //        .address("Cabildo # 123")
  //        .city("Buenos Aires")
  //        .country("ARGENTINA")
  //        .zipCode("1000")
  //        .size(SMALL)
  //        .lockerStatus(AVAILABLE)
  //        .build();
  //
  //    this.rackedLockersRepository.saveAndFlush(rackedLocker);
  //
  //    this.mockMvc
  //        .perform(MockMvcRequestBuilders
  //            .post(RESERVATIONS_URL)
  //            .contentType(MediaType.APPLICATION_JSON)
  //            .content(content.getContentAsByteArray()))
  //        .andExpect(status().isCreated())
  //        .andExpect(jsonPath("$.code").value(201))
  //        .andExpect(jsonPath("$.id").isNotEmpty())
  //        .andExpect(jsonPath("$.lockerSize").value("SMALL"))
  //        .andExpect(jsonPath("$.country").value("ARGENTINA"))
  //        .andExpect(jsonPath("$.currency").value("ARS"))
  //        .andExpect(jsonPath("$.price").value(23.5));
  //  }
}

