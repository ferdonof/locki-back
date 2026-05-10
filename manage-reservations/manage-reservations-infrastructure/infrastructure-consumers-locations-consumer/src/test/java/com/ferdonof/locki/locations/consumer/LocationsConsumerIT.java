package com.ferdonof.locki.locations.consumer;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.ferdonof.locki.base.DeadLetterService;
import com.ferdonof.locki.base.IdempotencyService;
import com.ferdonof.locki.locations.TestLocationsConsumerApplication;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = TestLocationsConsumerApplication.class)
class LocationsConsumerIT {

  @Container
  private static final GenericContainer<?> REDIS = new GenericContainer<>("redis:7-alpine")
      .withExposedPorts(6379);

  @DynamicPropertySource
  static void redisProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.redis.host", REDIS::getHost);
    registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
  }

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired
  private LocationsConsumer locationsConsumer;

  @Autowired
  private IdempotencyService idempotencyService;

  private static final String LOCATIONS_STREAM = "locations.events";

  private static final String LOCATIONS_GROUP = "locations-group";

  @BeforeEach
  void setUp() {
    this.redisTemplate.delete(LOCATIONS_STREAM);
    this.redisTemplate.delete(LOCATIONS_STREAM + ".dlq");
  }

  @Test
  void testLocationsConsumerProcessesEvent() {
    final Map<Object, Object> eventData = new HashMap<>();
    eventData.put("eventId", "location-event-1");
    eventData.put("locationId", "loc-123");
    eventData.put("name", "Downtown Locker");

    final RecordId recordId = this.redisTemplate
        .opsForStream()
        .add(StreamRecords
            .mapBacked(eventData)
            .withStreamKey(LOCATIONS_STREAM));

    assertNotNull(recordId);

    try {
      this.redisTemplate
          .opsForStream()
          .createGroup(LOCATIONS_STREAM, LOCATIONS_GROUP);
    } catch (final Exception ignored) {

    }

    final MapRecord<String, Object, Object> message = StreamRecords
        .mapBacked(eventData)
        .withStreamKey(LOCATIONS_STREAM)
        .withId(recordId);

    this.locationsConsumer.handle(LOCATIONS_GROUP, message);

    assertTrue(this.idempotencyService.isProcessed("location-event-1"));
  }

  @Test
  void testLocationsConsumerHandlesDuplicate() {
    final Map<Object, Object> eventData = new HashMap<>();
    eventData.put("eventId", "location-event-2");
    eventData.put("locationId", "loc-456");

    this.idempotencyService.markProcessed("location-event-2");

    final RecordId recordId = this.redisTemplate
        .opsForStream()
        .add(StreamRecords
            .mapBacked(eventData)
            .withStreamKey(LOCATIONS_STREAM));

    final MapRecord<String, Object, Object> message = StreamRecords
        .mapBacked(eventData)
        .withStreamKey(LOCATIONS_STREAM)
        .withId(recordId);

    assertDoesNotThrow(() -> this.locationsConsumer.handle(LOCATIONS_GROUP, message));

    assertTrue(this.idempotencyService.isProcessed("location-event-2"));
  }

  @Test
  void testLocationsConsumerMultipleEvents() throws InterruptedException {
    final int eventCount = 5;

    for (int i = 0; i < eventCount; i++) {
      final Map<Object, Object> eventData = new HashMap<>();
      eventData.put("eventId", "location-event-" + i);
      eventData.put("locationId", "loc-" + i);
      eventData.put("sequence", i);

      final RecordId recordId = this.redisTemplate
          .opsForStream()
          .add(StreamRecords
              .mapBacked(eventData)
              .withStreamKey(LOCATIONS_STREAM));

      final MapRecord<String, Object, Object> message = StreamRecords
          .mapBacked(eventData)
          .withStreamKey(LOCATIONS_STREAM)
          .withId(recordId);

      this.locationsConsumer.handle(LOCATIONS_GROUP, message);
    }

    await()
        .atMost(5, TimeUnit.SECONDS)
        .until(() -> {
          int processedCount = 0;
          for (int i = 0; i < eventCount; i++) {
            if (this.idempotencyService.isProcessed("location-event-" + i)) {
              processedCount++;
            }
          }
          return processedCount;
        }, result -> result == eventCount);
  }

  @Test
  void testLocationsConsumerSendsFailedEventToDLQ() {
    final DeadLetterService dlq = new DeadLetterService(this.redisTemplate);
    final IdempotencyService idempotencyService = new IdempotencyService();
    final LocationsConsumer failingConsumer = new LocationsConsumer(
        this.redisTemplate, idempotencyService, dlq) {
      @Override
      protected void handleProcessing(Map<Object, Object> data) {
        throw new RuntimeException("Simulated failure");
      }
    };

    final Map<Object, Object> eventData = new HashMap<>();
    eventData.put("eventId", "location-event-3");
    eventData.put("retries", "5");

    final RecordId recordId = this.redisTemplate
        .opsForStream()
        .add(StreamRecords
            .mapBacked(eventData)
            .withStreamKey(LOCATIONS_STREAM));

    final MapRecord<String, Object, Object> message = StreamRecords
        .mapBacked(eventData)
        .withStreamKey(LOCATIONS_STREAM)
        .withId(recordId);

    failingConsumer.handle(LOCATIONS_GROUP, message);

    final long dlqSize = this.redisTemplate
        .opsForStream()
        .size(LOCATIONS_STREAM + ".dlq");

    assertEquals(1, dlqSize);
  }

  @Test
  void testLocationsConsumerRetriesOnFailure() {
    final DeadLetterService dlq = new DeadLetterService(this.redisTemplate);
    final IdempotencyService idempotencyService = new IdempotencyService();

    final LocationsConsumer failingConsumer = new LocationsConsumer(
        this.redisTemplate, idempotencyService, dlq) {
      @Override
      protected void handleProcessing(Map<Object, Object> data) {
        throw new RuntimeException("Simulated failure");
      }
    };

    final Map<Object, Object> eventData = new HashMap<>();
    eventData.put("eventId", "location-event-retry");
    eventData.put("retries", "2");

    final RecordId recordId = this.redisTemplate
        .opsForStream()
        .add(StreamRecords
            .mapBacked(eventData)
            .withStreamKey(LOCATIONS_STREAM));

    final MapRecord<String, Object, Object> message = StreamRecords
        .mapBacked(eventData)
        .withStreamKey(LOCATIONS_STREAM)
        .withId(recordId);

    failingConsumer.handle(LOCATIONS_GROUP, message);

    final var allMessages = this.redisTemplate
        .opsForStream()
        .range(LOCATIONS_STREAM, Range.unbounded());

    assertNotNull(allMessages);
    final boolean hasRetry3 = allMessages
        .stream()
        .anyMatch(r -> "3".equals(r
            .getValue()
            .get("retries")
            .toString()));
    assertTrue(hasRetry3);
  }
}

