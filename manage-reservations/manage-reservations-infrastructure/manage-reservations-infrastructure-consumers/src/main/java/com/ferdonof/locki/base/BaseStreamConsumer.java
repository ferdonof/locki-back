package com.ferdonof.locki.base;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseStreamConsumer {

  private final RedisTemplate<String, Object> redisTemplate;

  private final IdempotencyService idempotencyService;

  private final DeadLetterService dlq;

  public void handle(String group, MapRecord<String, Object, Object> message) {

    final Map<Object, Object> data = message.getValue();
    final String eventId = (String) data.get("eventId");

    if (this.idempotencyService.isProcessed(eventId)) {
      log.debug("Event already processed: {}", eventId);
      this.ack(group, message);
      return;
    }

    try {
      this.routeEvent(data);

      this.idempotencyService.markProcessed(eventId);
      this.ack(group, message);
      log.info("Event processed successfully: {}", eventId);
    } catch (final Exception e) {

      final int retries = this.getRetries(data);
      log.warn("Error processing event: {}. Retries: {}", eventId, retries, e);

      if (retries >= 5) {
        log.error("Max retries exceeded for event: {}. Sending to DLQ", eventId);
        this.dlq.send(message.getStream(), data);
        this.ack(group, message);
      } else {
        data.put("retries", retries + 1);
        this.requeueMessage(message.getStream(), data);
        this.ack(group, message);
      }
    }
  }

  private void ack(String group, MapRecord<String, Object, Object> message) {
    this.redisTemplate
        .opsForStream()
        .acknowledge(group, message);
  }

  private int getRetries(Map<Object, Object> data) {
    final Object retriesObj = data.getOrDefault("retries", "0");
    try {
      return Integer.parseInt((String) retriesObj);
    } catch (final NumberFormatException e) {
      log.warn("Invalid retries value: {}", retriesObj);
      return 0;
    }
  }

  private void routeEvent(Map<Object, Object> data) {
    this.handleProcessing(data);
  }

  private void requeueMessage(String stream, Map<Object, Object> data) {
    this.redisTemplate
        .opsForStream()
        .add(StreamRecords
            .mapBacked(data)
            .withStreamKey(stream));
  }

  protected abstract void handleProcessing(Map<Object, Object> data);
}




