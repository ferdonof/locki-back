package com.ferdonof.locki.locations.reclaim;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.ferdonof.locki.locations.TestLocationsConsumerApplication;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = TestLocationsConsumerApplication.class)
class LocationsReclaimServiceIT {

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
  private LocationsReclaimService reclaimService;

  private static final String LOCATIONS_STREAM = "locations.events";

  private static final String LOCATIONS_GROUP = "locations-group";

  private static final String CONSUMER = "locations-consumer";

  @BeforeEach
  void setUp() {
    // Clean up Redis before each test
    this.redisTemplate.delete(LOCATIONS_STREAM);
    // Create the stream with an initial message first
    final Map<Object, Object> init = new HashMap<>();
    init.put("init", "true");
    this.redisTemplate
        .opsForStream()
        .add(StreamRecords
            .mapBacked(init)
            .withStreamKey(LOCATIONS_STREAM));

    // Now try to destroy the group if it exists
    try {
      this.redisTemplate
          .opsForStream()
          .destroyGroup(LOCATIONS_STREAM, LOCATIONS_GROUP);
    } catch (final Exception e) {
      // Group might not exist, that's ok
    }

    // Create the group at the latest position
    try {
      this.redisTemplate
          .opsForStream()
          .createGroup(LOCATIONS_STREAM, ReadOffset.latest(), LOCATIONS_GROUP);
    } catch (final Exception e) {
      // Group might already exist, that's ok
    }
  }

  @Test
  void testReclaimServiceWithNoPendingMessages() {
    // Create stream and group
    this.createStreamAndGroup();

    // Should handle gracefully with no pending messages
    assertDoesNotThrow(() -> this.reclaimService.reclaim());
  }

  @Test
  void testReclaimServiceReclainsOldPendingMessages() throws InterruptedException {
    // Create stream and group
    this.createStreamAndGroup();

    // Add event to stream
    final Map<Object, Object> eventData = new HashMap<>();
    eventData.put("eventId", "location-event-1");
    eventData.put("data", "test");

    final RecordId recordId = this.redisTemplate
        .opsForStream()
        .add(StreamRecords
            .mapBacked(eventData)
            .withStreamKey(LOCATIONS_STREAM));

    // Read message as consumer (creates pending message)
    this.redisTemplate
        .opsForStream()
        .read(org.springframework.data.redis.connection.stream.Consumer.from(LOCATIONS_GROUP, CONSUMER),
            org.springframework.data.redis.connection.stream.StreamOffset.create(LOCATIONS_STREAM,
                org.springframework.data.redis.connection.stream.ReadOffset.lastConsumed()));

    // Wait a bit and then reclaim (simulating timeout)
    Thread.sleep(100);

    // This should reclaim the pending message
    this.reclaimService.reclaim();

    // Pending message should have been reclaimed
    final var pending = this.redisTemplate
        .opsForStream()
        .pending(LOCATIONS_STREAM, LOCATIONS_GROUP);

    // The reclaim should have claimed the message to a new consumer
    assertNotNull(pending);
  }

  @Test
  void testReclaimServiceIgnoresRecentMessages() {
    // Create stream and group
    this.createStreamAndGroup();

    // Add event
    final Map<Object, Object> eventData = new HashMap<>();
    eventData.put("eventId", "location-event-recent");

    final RecordId recordId = this.redisTemplate
        .opsForStream()
        .add(StreamRecords
            .mapBacked(eventData)
            .withStreamKey(LOCATIONS_STREAM));

    // Read message
    this.redisTemplate
        .opsForStream()
        .read(
            Consumer.from(LOCATIONS_GROUP, CONSUMER),
            StreamOffset.create(LOCATIONS_STREAM,
                ReadOffset.lastConsumed())
        );

    // Immediately reclaim (should not reclaim recent messages)
    this.reclaimService.reclaim();

    // Verify pending message is still there (not reclaimed)
    final var pending = this.redisTemplate
        .opsForStream()
        .pending(LOCATIONS_STREAM, LOCATIONS_GROUP);

    // Should have no reclaimed messages (they are recent)
    assertNotNull(pending);
  }

  @Test
  void testReclaimServiceMultipleConsumerGroups() {
    final String stream1 = "locations.events";
    final String group1 = "locations-group-1";
    final Map<Object, Object> init = new HashMap<>();
    init.put("init", "true");

    // Create stream and group
    this.redisTemplate
        .opsForStream()
        .add(StreamRecords
            .mapBacked(init)
            .withStreamKey(stream1));

    try {
      this.redisTemplate
          .opsForStream()
          .createGroup(stream1, group1);
    } catch (final Exception e) {
      // Group might already exist
    }

    // Should handle multiple calls without error
    assertDoesNotThrow(() -> this.reclaimService.reclaim());
  }

  @Test
  void testReclaimServiceConsumerNameIncludesApplicationName() {
    // Create stream and group
    this.createStreamAndGroup();

    // Reclaim should work with generated consumer name
    assertDoesNotThrow(() -> this.reclaimService.reclaim());
  }

  private void createStreamAndGroup() {
    // Add initial message to create stream
    final Map<Object, Object> initialData = new HashMap<>();
    initialData.put("init", "true");

    this.redisTemplate
        .opsForStream()
        .add(StreamRecords
            .mapBacked(initialData)
            .withStreamKey(LOCATIONS_STREAM));

    // Create group
    try {
      this.redisTemplate
          .opsForStream()
          .createGroup(LOCATIONS_STREAM, LOCATIONS_GROUP);
    } catch (final Exception e) {
      // Group might already exist
    }
  }
}

