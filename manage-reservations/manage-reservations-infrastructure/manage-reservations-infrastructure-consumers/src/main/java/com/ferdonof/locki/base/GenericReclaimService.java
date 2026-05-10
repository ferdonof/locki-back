package com.ferdonof.locki.base;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class GenericReclaimService {

  private final RedisTemplate<String, Object> redisTemplate;

  @Value("${spring.application.name:locki-back}")
  private String consumerName;

  public abstract void reclaim();

  protected void reclaimStream(String stream, String group) {

    final PendingMessages pending = this.redisTemplate
        .opsForStream()
        .pending(
            stream,
            group,
            Range.unbounded(),
            100
        );

    if (pending == null || pending.isEmpty()) {
      return;
    }

    final String consumer = this.getConsumerName();
    int reclaimedCount = 0;

    for (final PendingMessage msg : pending) {

      if (msg
          .getElapsedTimeSinceLastDelivery()
          .toSeconds() > 60) {

        try {
          this.redisTemplate
              .opsForStream()
              .claim(
                  stream,
                  group,
                  consumer,
                  Duration.ofSeconds(60),
                  msg.getId()
              );
          reclaimedCount++;
        } catch (final Exception e) {
          log.error("Failed to reclaim message {} from stream {}", msg.getId(), stream, e);
        }
      }
    }

    if (reclaimedCount > 0) {
      log.info("Reclaimed {} pending messages from stream: {}", reclaimedCount, stream);
    }
  }

  private String getConsumerName() {
    return consumerName + "-" + UUID.randomUUID().toString().substring(0, 8);
  }
}
