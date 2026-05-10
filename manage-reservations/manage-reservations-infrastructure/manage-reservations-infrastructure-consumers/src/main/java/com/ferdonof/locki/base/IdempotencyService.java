package com.ferdonof.locki.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class IdempotencyService {

  private final Map<String, Long> processed = new ConcurrentHashMap<>();

  @Value("${idempotency.ttl.minutes:1440}")
  private long ttlMinutes;

  public boolean isProcessed(String eventId) {
    return this.processed.containsKey(eventId);
  }

  public void markProcessed(String eventId) {
    this.processed.put(eventId, System.currentTimeMillis());
  }

  @Scheduled(fixedDelay = 300000)
  public void cleanExpiredEntries() {
    final long now = System.currentTimeMillis();
    final long ttlMillis = this.ttlMinutes * 60 * 1000;

    this.processed
        .entrySet()
        .removeIf(entry ->
            (now - entry.getValue()) > ttlMillis
        );
  }
}