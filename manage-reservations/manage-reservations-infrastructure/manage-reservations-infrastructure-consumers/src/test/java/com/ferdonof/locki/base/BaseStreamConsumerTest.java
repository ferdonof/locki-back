package com.ferdonof.locki.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;

@ExtendWith(MockitoExtension.class)
class BaseStreamConsumerTest {

  @Mock
  private RedisTemplate<String, Object> redisTemplate;

  @Mock
  private IdempotencyService idempotencyService;

  @Mock
  private DeadLetterService deadLetterService;

  @Mock
  private StreamOperations<String, Object, Object> streamOperations;

  private TestableStreamConsumer consumer;

  private static final String GROUP = "test-group";

  private static final String STREAM_KEY = "test.events";

  private static class TestableStreamConsumer extends BaseStreamConsumer {

    private RuntimeException exceptionToThrow;

    TestableStreamConsumer(
        RedisTemplate<String, Object> redisTemplate,
        IdempotencyService idempotencyService,
        DeadLetterService dlq) {
      super(redisTemplate, idempotencyService, dlq);
    }

    void willThrow(RuntimeException e) {
      this.exceptionToThrow = e;
    }

    void willSucceed() {
      this.exceptionToThrow = null;
    }

    @Override
    protected void handleProcessing(Map<Object, Object> data) {
      if (this.exceptionToThrow != null) {
        throw this.exceptionToThrow;
      }
    }
  }

  @BeforeEach
  void setUp() {
    this.consumer = new TestableStreamConsumer(this.redisTemplate, this.idempotencyService, this.deadLetterService);
    when(this.redisTemplate.opsForStream()).thenReturn(this.streamOperations);
  }

  private MapRecord<String, Object, Object> buildMessage(Map<Object, Object> data) {
    return StreamRecords
        .mapBacked(data)
        .withStreamKey(STREAM_KEY)
        .withId(RecordId.of("0-1"));
  }

  @Test
  void testHandleSuccessfullyProcessedEvent() {
    this.consumer.willSucceed();

    final Map<Object, Object> data = new HashMap<>();
    data.put("eventId", "evt-success");

    when(this.idempotencyService.isProcessed("evt-success")).thenReturn(false);

    this.consumer.handle(GROUP, this.buildMessage(data));

    verify(this.idempotencyService).markProcessed("evt-success");
    verify(this.streamOperations).acknowledge(eq(GROUP), any(MapRecord.class));
    verify(this.deadLetterService, never()).send(anyString(), any());
    verify(this.streamOperations, never()).add(any());
  }

  @Test
  void testHandleEventWithRetries() {
    this.consumer.willThrow(new RuntimeException("transient error"));

    final Map<Object, Object> data = new HashMap<>();
    data.put("eventId", "evt-retry");
    data.put("retries", "2");

    when(this.idempotencyService.isProcessed("evt-retry")).thenReturn(false);

    final ArgumentCaptor<MapRecord<String, Object, Object>> requeueCaptor =
        ArgumentCaptor.forClass(MapRecord.class);

    this.consumer.handle(GROUP, this.buildMessage(data));

    verify(this.deadLetterService, never()).send(anyString(), any());

    verify(this.streamOperations).add(requeueCaptor.capture());
    final Map<Object, Object> requeuedData = requeueCaptor
        .getValue()
        .getValue();
    assertThat(requeuedData
        .get("retries")
        .toString()).isEqualTo("3");

    verify(this.streamOperations).acknowledge(eq(GROUP), any(MapRecord.class));
  }

  @Test
  void testHandleMaxRetriesExceeded() {
    this.consumer.willThrow(new RuntimeException("persistent error"));

    final Map<Object, Object> data = new HashMap<>();
    data.put("eventId", "evt-dlq");
    data.put("retries", "5");

    when(this.idempotencyService.isProcessed("evt-dlq")).thenReturn(false);

    this.consumer.handle(GROUP, this.buildMessage(data));

    verify(this.deadLetterService).send(eq(STREAM_KEY), eq(data));

    verify(this.streamOperations, never()).add(any());

    verify(this.streamOperations).acknowledge(eq(GROUP), any(MapRecord.class));
  }

  @Test
  void testHandleNoRetriesValueDefaultsToZero() {
    this.consumer.willThrow(new RuntimeException("error"));

    final Map<Object, Object> data = new HashMap<>();
    data.put("eventId", "evt-no-retries");

    when(this.idempotencyService.isProcessed("evt-no-retries")).thenReturn(false);

    final ArgumentCaptor<MapRecord<String, Object, Object>> requeueCaptor =
        ArgumentCaptor.forClass(MapRecord.class);

    this.consumer.handle(GROUP, this.buildMessage(data));

    verify(this.deadLetterService, never()).send(anyString(), any());
    verify(this.streamOperations).add(requeueCaptor.capture());

    final Map<Object, Object> requeuedData = requeueCaptor
        .getValue()
        .getValue();
    assertThat(requeuedData
        .get("retries")
        .toString()).isEqualTo("1");
  }

  @Test
  void handle_whenInvalidRetriesValue() {
    this.consumer.willThrow(new RuntimeException("error"));

    final Map<Object, Object> data = new HashMap<>();
    data.put("eventId", "evt-bad-retries");
    data.put("retries", "not-a-number");

    when(this.idempotencyService.isProcessed("evt-bad-retries")).thenReturn(false);

    assertThatNoException().isThrownBy(() -> this.consumer.handle(GROUP, this.buildMessage(data)));

    verify(this.deadLetterService, never()).send(anyString(), any());
    verify(this.streamOperations).acknowledge(eq(GROUP), any(MapRecord.class));
  }

  @Test
  void handle_withDuplicateEvent_thenSkip() {
    when(this.idempotencyService.isProcessed("evt-dup")).thenReturn(true);

    final Map<Object, Object> data = new HashMap<>();
    data.put("eventId", "evt-dup");

    this.consumer.handle(GROUP, this.buildMessage(data));

    verify(this.idempotencyService, never()).markProcessed(anyString());
    verify(this.deadLetterService, never()).send(anyString(), any());
    verify(this.streamOperations, never()).add(any());
    verify(this.streamOperations).acknowledge(eq(GROUP), any(MapRecord.class));
  }
}

