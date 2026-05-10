package com.ferdonof.locki.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class IdempotencyServiceTest {

  private IdempotencyService idempotencyService;

  @BeforeEach
  void setUp() {
    this.idempotencyService = new IdempotencyService();
    ReflectionTestUtils.setField(this.idempotencyService, "ttlMinutes", 1440L);
  }

  @Test
  void testMarkAndCheckProcessed() {
    final String eventId = "event-1";

    // Initially not processed
    assertFalse(this.idempotencyService.isProcessed(eventId));

    // Mark as processed
    this.idempotencyService.markProcessed(eventId);

    // Should be processed now
    assertTrue(this.idempotencyService.isProcessed(eventId));
  }

  @Test
  void testMultipleEventsProcessed() {
    final String eventId1 = "event-1";
    final String eventId2 = "event-2";
    final String eventId3 = "event-3";

    // Mark multiple events
    this.idempotencyService.markProcessed(eventId1);
    this.idempotencyService.markProcessed(eventId2);
    this.idempotencyService.markProcessed(eventId3);

    // All should be marked
    assertTrue(this.idempotencyService.isProcessed(eventId1));
    assertTrue(this.idempotencyService.isProcessed(eventId2));
    assertTrue(this.idempotencyService.isProcessed(eventId3));

    // Unprocessed event should be false
    assertFalse(this.idempotencyService.isProcessed("event-4"));
  }

  @Test
  void testCleanExpiredEntriesRemovesOldEvents() {
    final String eventId = "old-event";
    ReflectionTestUtils.setField(this.idempotencyService, "ttlMinutes", 1L);

    // Mark event
    this.idempotencyService.markProcessed(eventId);
    assertTrue(this.idempotencyService.isProcessed(eventId));

    // Simulate waiting (backdating the timestamp)
    @SuppressWarnings("unchecked") final java.util.Map<String, Long> processed =
        (java.util.Map<String, Long>) ReflectionTestUtils.getField(this.idempotencyService, "processed");
    if (processed != null) {
      processed.put(eventId, System.currentTimeMillis() - 120000); // 2 minutes ago
    }

    // Clean expired entries
    this.idempotencyService.cleanExpiredEntries();

    // Old event should be removed
    assertFalse(this.idempotencyService.isProcessed(eventId));
  }

  @Test
  void testCleanExpiredEntriesKeepsRecentEvents() {
    final String recentEvent = "recent-event";
    final String oldEvent = "old-event";
    ReflectionTestUtils.setField(this.idempotencyService, "ttlMinutes", 60L);

    // Mark both events
    this.idempotencyService.markProcessed(recentEvent);
    this.idempotencyService.markProcessed(oldEvent);

    // Backdate old event to 90 minutes ago
    @SuppressWarnings("unchecked") final java.util.Map<String, Long> processed =
        (java.util.Map<String, Long>) ReflectionTestUtils.getField(this.idempotencyService, "processed");
    if (processed != null) {
      processed.put(oldEvent, System.currentTimeMillis() - 5400000);
    }

    // Clean expired entries
    this.idempotencyService.cleanExpiredEntries();

    // Recent event should remain
    assertTrue(this.idempotencyService.isProcessed(recentEvent));

    // Old event should be removed
    assertFalse(this.idempotencyService.isProcessed(oldEvent));
  }

  @Test
  void testProcessedMapThreadSafe() throws InterruptedException {
    final int numThreads = 10;
    final int eventsPerThread = 100;

    final Thread[] threads = new Thread[numThreads];
    for (int i = 0; i < numThreads; i++) {
      final int threadId = i;
      threads[i] = new Thread(() -> {
        for (int j = 0; j < eventsPerThread; j++) {
          final String eventId = "event-" + threadId + "-" + j;
          this.idempotencyService.markProcessed(eventId);
          assertTrue(this.idempotencyService.isProcessed(eventId));
        }
      });
    }

    for (final Thread thread : threads) {
      thread.start();
    }

    for (final Thread thread : threads) {
      thread.join();
    }

    // Verify all events were added
    @SuppressWarnings("unchecked") final java.util.Map<String, Long> processed =
        (java.util.Map<String, Long>) ReflectionTestUtils.getField(this.idempotencyService, "processed");
    if (processed != null) {
      assertEquals(numThreads * eventsPerThread, processed.size());
    }
  }
}


