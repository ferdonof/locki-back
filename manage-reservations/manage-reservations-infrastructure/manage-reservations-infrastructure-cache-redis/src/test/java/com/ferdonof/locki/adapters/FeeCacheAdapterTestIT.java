package com.ferdonof.locki.adapters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.ferdonof.locki.fee.entities.Fee;
import com.ferdonof.locki.lockers.enums.LockerSize;

@Tag("integration")
@SpringBootTest
@Testcontainers
class FeeCacheAdapterTestIT {

  private static final String COUNTRY = "ARGENTINA";

  private static final String CURRENCY = "ARS";

  private static final LockerSize LOCKER_SIZE = LockerSize.SMALL;

  private static final BigDecimal PRICE = new BigDecimal("25.50");

  private static final String KEY_PREFIX = "fee:%s:%s";

  @Container
  @ServiceConnection(name = "redis")
  static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
      .withExposedPorts(6379);

  @Autowired
  private FeeCacheAdapter feeCacheAdapter;

  @Autowired
  private StringRedisTemplate redisTemplate;

  @BeforeEach
  void setUp() {
    this.redisTemplate.execute((RedisCallback<Void>) connection -> {
      connection
          .serverCommands()
          .flushAll();
      return null;
    });
  }

  @Test
  void put_whenValidFee_shouldStoreSerializedJsonInRedis() {
    final UUID id = UUID.randomUUID();
    final Fee fee = this.buildFee(id, LOCKER_SIZE, COUNTRY, CURRENCY, PRICE);

    this.feeCacheAdapter.put(fee);

    final String key = KEY_PREFIX.formatted(COUNTRY, LOCKER_SIZE);
    final String stored = this.redisTemplate
        .opsForValue()
        .get(key);

    assertThat(stored).isNotNull();
    assertThat(stored).contains(id.toString());
    assertThat(stored).contains(COUNTRY);
    assertThat(stored).contains(CURRENCY);
    assertThat(stored).contains("SMALL");
  }

  @Test
  void put_whenNullFee_shouldNotStoreAnything() {
    this.feeCacheAdapter.put(null);

    final Long keyCount = this.redisTemplate.execute((RedisCallback<Long>) conn ->
        conn
            .serverCommands()
            .dbSize());

    assertThat(keyCount).isZero();
  }

  @Test
  void put_whenFeeHasNullCountry_shouldNotStore() {
    final Fee fee = this.buildFee(UUID.randomUUID(), LOCKER_SIZE, null, CURRENCY, PRICE);

    this.feeCacheAdapter.put(fee);

    final Long keyCount = this.redisTemplate.execute((RedisCallback<Long>) conn ->
        conn
            .serverCommands()
            .dbSize());

    assertThat(keyCount).isZero();
  }

  @Test
  void put_whenFeeHasNullCurrency_shouldNotStore() {
    final Fee fee = this.buildFee(UUID.randomUUID(), LOCKER_SIZE, COUNTRY, null, PRICE);

    this.feeCacheAdapter.put(fee);

    final Long keyCount = this.redisTemplate.execute((RedisCallback<Long>) conn ->
        conn
            .serverCommands()
            .dbSize());

    assertThat(keyCount).isZero();
  }

  @Test
  void put_whenFeeHasNullLockerSize_shouldNotStore() {
    final Fee fee = this.buildFee(UUID.randomUUID(), null, COUNTRY, CURRENCY, PRICE);

    this.feeCacheAdapter.put(fee);

    final Long keyCount = this.redisTemplate.execute((RedisCallback<Long>) conn ->
        conn
            .serverCommands()
            .dbSize());

    assertThat(keyCount).isZero();
  }

  @Test
  void get_whenFeeIsCached_shouldReturnHydratedFee() {
    final UUID id = UUID.randomUUID();
    final Fee fee = this.buildFee(id, LOCKER_SIZE, COUNTRY, CURRENCY, PRICE);
    this.feeCacheAdapter.put(fee);

    final Optional<Fee> result = this.feeCacheAdapter.get(fee);

    assertThat(result).isPresent();
    assertThat(result
        .get()
        .id()).isEqualTo(id);
    assertThat(result
        .get()
        .lockerSize()).isEqualTo(LOCKER_SIZE);
    assertThat(result
        .get()
        .country()).isEqualTo(COUNTRY);
    assertThat(result
        .get()
        .currency()).isEqualTo(CURRENCY);
    assertThat(result
        .get()
        .price()).isEqualByComparingTo(PRICE);
  }

  @Test
  void get_whenFeeIsNotCached_shouldReturnEmpty() {
    final Fee fee = this.buildFee(null, LOCKER_SIZE, COUNTRY, CURRENCY, PRICE);

    final Optional<Fee> result = this.feeCacheAdapter.get(fee);

    assertThat(result).isEmpty();
  }

  @Test
  void get_whenStoredValueIsCorrupted_shouldReturnEmpty() {
    final Fee fee = this.buildFee(null, LOCKER_SIZE, COUNTRY, CURRENCY, PRICE);
    final String key = KEY_PREFIX.formatted(COUNTRY, LOCKER_SIZE);
    this.redisTemplate
        .opsForValue()
        .set(key, "NOT_VALID_JSON");

    final Optional<Fee> result = this.feeCacheAdapter.get(fee);

    assertThat(result).isEmpty();
  }

  @Test
  void evictCache_whenFeeIsCached_shouldRemoveKeyFromRedis() {
    final Fee fee = this.buildFee(UUID.randomUUID(), LOCKER_SIZE, COUNTRY, CURRENCY, PRICE);
    this.feeCacheAdapter.put(fee);

    final String key = KEY_PREFIX.formatted(COUNTRY, LOCKER_SIZE);
    assertThat(this.redisTemplate
        .opsForValue()
        .get(key)).isNotNull();

    this.feeCacheAdapter.evictCache(fee);

    assertThat(this.redisTemplate
        .opsForValue()
        .get(key)).isNull();
  }

  @Test
  void evictCache_whenNullFee_shouldNotThrow() {
    assertThatNoException().isThrownBy(() -> this.feeCacheAdapter.evictCache(null));

    final Long keyCount = this.redisTemplate.execute((RedisCallback<Long>) conn ->
        conn
            .serverCommands()
            .dbSize());

    assertThat(keyCount).isZero();
  }

  @Test
  void evictCache_whenFeeNotCached_shouldNotThrow() {
    final Fee fee = this.buildFee(UUID.randomUUID(), LOCKER_SIZE, COUNTRY, CURRENCY, PRICE);

    assertThatNoException().isThrownBy(() -> this.feeCacheAdapter.evictCache(fee));
  }

  private Fee buildFee(UUID id, LockerSize lockerSize, String country, String currency, BigDecimal price) {
    return Fee
        .builder()
        .id(id)
        .lockerSize(lockerSize)
        .country(country)
        .currency(currency)
        .price(price)
        .build();
  }
}