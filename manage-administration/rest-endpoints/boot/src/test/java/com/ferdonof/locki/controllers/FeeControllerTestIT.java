package com.ferdonof.locki.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.ferdonof.locki.entities.FeeEntity;
import com.ferdonof.locki.lockers.enums.LockerSize;
import com.ferdonof.locki.repositories.FeeRepository;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class FeeControllerTestIT {

  private static final String FEES_URL = "/admin/fees";

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
  private FeeRepository feeRepository;

  @Autowired
  private StringRedisTemplate redisTemplate;

  @BeforeEach
  void setUp() {
    this.feeRepository.deleteAll();
    this.redisTemplate.execute((RedisCallback<Void>) connection -> {
      connection
          .serverCommands()
          .flushAll();
      return null;
    });
  }

  private String feeCacheKey(FeeEntity fee) {
    return "fee:%s:%s:%s".formatted(fee.getCountry(), fee.getCurrency(), fee.getLockerSize());
  }

  private FeeEntity persistFee(LockerSize size, String country, String currency, BigDecimal price) {
    return this.feeRepository.saveAndFlush(FeeEntity
        .builder()
        .lockerSize(size)
        .country(country)
        .currency(currency)
        .price(price)
        .build());
  }

  @Test
  void create_whenValidRequest_shouldReturn201AndPersistFee() throws Exception {
    final ClassPathResource content = new ClassPathResource("mocks/requests/fees/create-fee.json");
    this.mockMvc
        .perform(post(FEES_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content.getContentAsByteArray()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.lockerSize").value("SMALL"))
        .andExpect(jsonPath("$.country").value("ARGENTINA"))
        .andExpect(jsonPath("$.currency").value("ARS"))
        .andExpect(jsonPath("$.price").value(23.5));

    assertThat(this.feeRepository.count()).isEqualTo(1);
    final var persisted = this.feeRepository
        .findAll()
        .getFirst();
    assertThat(persisted.getLockerSize()).isEqualTo(LockerSize.SMALL);
    assertThat(persisted.getCountry()).isEqualTo("ARGENTINA");
    assertThat(persisted.getCurrency()).isEqualTo("ARS");
    assertThat(persisted.getPrice()).isEqualByComparingTo("23.50");

    final String cachedValue = this.redisTemplate
        .opsForValue()
        .get(this.feeCacheKey(persisted));
    assertThat(cachedValue).isNotBlank();
    assertThat(cachedValue).contains("\"id\":\"" + persisted.getId() + "\"");
    assertThat(cachedValue).contains("\"country\":\"ARGENTINA\"");
    assertThat(cachedValue).contains("\"currency\":\"ARS\"");
    assertThat(cachedValue).contains("\"lockerSize\":\"SMALL\"");
  }

  @Test
  void create_whenMissingRequiredAttributes_shouldReturn400() throws Exception {
    this.mockMvc
        .perform(post(FEES_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isBadRequest());

    assertThat(this.feeRepository.count()).isZero();
  }

  @Test
  void create_whenDuplicatedFeeByUniqueConstraint_shouldReturn400() throws Exception {
    final ClassPathResource content = new ClassPathResource("mocks/requests/fees/duplicate-fee.json");
    this.persistFee(LockerSize.MEDIUM, "SPAIN", "EUR", BigDecimal.valueOf(10.00));

    this.mockMvc
        .perform(post(FEES_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content.getContentAsByteArray()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400));

    assertThat(this.feeRepository.count()).isEqualTo(1);
  }

  @Test
  void update_whenValidRequest_shouldReturn200AndUpdateFee() throws Exception {
    final ClassPathResource content = new ClassPathResource("mocks/requests/fees/update-fee-ok.json");
    final var persisted = this.persistFee(LockerSize.SMALL, "MEXICO", "MXN", BigDecimal.valueOf(5.00));

    this.mockMvc
        .perform(put(FEES_URL + "/{id}", persisted.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(content.getContentAsByteArray()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(persisted
            .getId()
            .toString()))
        .andExpect(jsonPath("$.lockerSize").value("LARGE"))
        .andExpect(jsonPath("$.price").value(30.0));

    final var updated = this.feeRepository
        .findById(persisted.getId())
        .orElseThrow();
    assertThat(updated.getLockerSize()).isEqualTo(LockerSize.LARGE);
    assertThat(updated.getPrice()).isEqualByComparingTo("30.00");
  }

  @Test
  void delete_whenFeeExists_shouldReturn204AndDeleteFee() throws Exception {
    final var persisted = this.persistFee(LockerSize.MEDIUM, "CHILE", "CLP", BigDecimal.valueOf(8.00));
    this.redisTemplate
        .opsForValue()
        .set(this.feeCacheKey(persisted), "seed");

    assertThat(this.redisTemplate
        .opsForValue()
        .get(this.feeCacheKey(persisted))).isEqualTo("seed");

    this.mockMvc
        .perform(delete(FEES_URL + "/{id}", persisted.getId()))
        .andExpect(status().isNoContent());

    assertThat(this.feeRepository.findById(persisted.getId())).isEmpty();
    assertThat(this.feeRepository.count()).isZero();
    assertThat(this.redisTemplate
        .opsForValue()
        .get(this.feeCacheKey(persisted))).isNull();
  }

  @Test
  void search_whenFeesExistForGivenCountryAndCurrency_shouldReturnMatchingFees() throws Exception {
    final ClassPathResource content = new ClassPathResource("mocks/requests/fees/search-fees.json");
    this.persistFee(LockerSize.SMALL, "ARGENTINA", "ARS", BigDecimal.valueOf(10.00));
    this.persistFee(LockerSize.LARGE, "ARGENTINA", "ARS", BigDecimal.valueOf(20.00));
    this.persistFee(LockerSize.MEDIUM, "SPAIN", "EUR", BigDecimal.valueOf(30.00));

    this.mockMvc
        .perform(get(FEES_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content.getContentAsByteArray()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id").isNotEmpty())
        .andExpect(jsonPath("$[1].id").isNotEmpty())
        .andExpect(jsonPath("$[0].country").value("ARGENTINA"))
        .andExpect(jsonPath("$[1].country").value("ARGENTINA"));
  }

  @Test
  void search_whenEmptyBody_shouldReturnAllFees() throws Exception {
    this.persistFee(LockerSize.SMALL, "ARGENTINA", "ARS", BigDecimal.valueOf(10.00));
    this.persistFee(LockerSize.LARGE, "SPAIN", "EUR", BigDecimal.valueOf(20.00));

    this.mockMvc
        .perform(get(FEES_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  void update_whenIdDoesNotExist_shouldCreateWithProvidedIdAndReturn404() throws Exception {
    final ClassPathResource content = new ClassPathResource("mocks/requests/fees/update-fee.json");
    final var randomId = UUID.randomUUID();

    this.mockMvc
        .perform(put(FEES_URL + "/{id}", randomId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content.getContentAsByteArray()))
        .andExpect(status().isNotFound());

    assertThat(this.feeRepository.findById(randomId)).isNotPresent();
  }
}

