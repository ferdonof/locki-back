package com.ferdonof.locki.base;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;

@ExtendWith(MockitoExtension.class)
class DeadLetterServiceTest {

  @Mock
  private RedisTemplate<String, Object> redisTemplate;

  @Mock
  private StreamOperations<String, Object, Object> streamOperations;

  private DeadLetterService deadLetterService;

  @BeforeEach
  void setUp() {
    when(this.redisTemplate.opsForStream()).thenReturn(this.streamOperations);
    this.deadLetterService = new DeadLetterService(this.redisTemplate);
  }

  @Test
  void testSendCreatesCorrectDLQStreamName() {
    final String originalStream = "orders.events";
    final Map<Object, Object> data = new HashMap<>();
    data.put("eventId", "event-123");
    data.put("status", "pending");

    this.deadLetterService.send(originalStream, data);

    // Verify that add was called with correct DLQ stream
    verify(this.streamOperations).add(any());

    // Should append .dlq to stream name
    assertEquals("orders.events.dlq", originalStream + ".dlq");
  }

  @Test
  void testSendToMultipleStreams() {
    final Map<Object, Object> data = new HashMap<>();
    data.put("eventId", "event-123");

    // Send to different streams
    this.deadLetterService.send("stream-1.events", data);
    this.deadLetterService.send("stream-2.events", data);
    this.deadLetterService.send("stream-3.events", data);

    // Verify add was called 3 times
    verify(this.streamOperations, times(3)).add(any());
  }

  @Test
  void testSendWithComplexData() {
    final String originalStream = "orders.events";
    final Map<Object, Object> complexData = new HashMap<>();
    complexData.put("eventId", "event-456");
    complexData.put("orderId", "order-789");
    complexData.put("amount", 99.99);
    complexData.put("retries", "2");

    this.deadLetterService.send(originalStream, complexData);

    verify(this.streamOperations).add(any());
  }

  @Test
  void testSendHandlesNullData() {
    final String originalStream = "test.events";
    final Map<Object, Object> data = null;

    // Should not throw exception
    assertDoesNotThrow(() -> this.deadLetterService.send(originalStream, data));
  }
}

