package com.ferdonof.locki.base;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class GenericReclaimServiceTest {

  @Mock
  private RedisTemplate<String, Object> redisTemplate;

  @Mock
  private StreamOperations<String, Object, Object> streamOperations;

  private TestReclaimService reclaimService;

  private static final String STREAM = "test.stream";

  private static final String GROUP = "test-group";

  @BeforeEach
  void setUp() {
    when(this.redisTemplate.opsForStream()).thenReturn(this.streamOperations);
    this.reclaimService = new TestReclaimService(this.redisTemplate);
    ReflectionTestUtils.setField(this.reclaimService, "consumerName", "test-consumer");
  }

  @Test
  void reclaimStream_withNoPendingMessages_claimNothing() {
    when(this.streamOperations.pending(
        anyString(),
        anyString(),
        any(Range.class),
        anyLong()
    )).thenReturn(null);

    this.reclaimService.reclaimStream(STREAM, GROUP);

    verify(this.streamOperations, times(1))
        .pending(anyString(), anyString(), any(Range.class), anyLong());

    verify(this.streamOperations, never())
        .claim(anyString(), anyString(), anyString(), any(Duration.class), any(RecordId.class));
  }

  @Test
  void reclaimStream_withEmptyPendingMessages_claimNothing() {
    final PendingMessages emptyPending = new PendingMessages(GROUP, new ArrayList<>());

    when(this.streamOperations.pending(
        anyString(),
        anyString(),
        any(Range.class),
        anyLong()
    )).thenReturn(emptyPending);

    this.reclaimService.reclaimStream(STREAM, GROUP);

    verify(this.streamOperations, times(1))
        .pending(anyString(), anyString(), any(Range.class), anyLong());

    verify(this.streamOperations, never())
        .claim(anyString(), anyString(), anyString(), any(Duration.class), any(RecordId.class));
  }

  @Test
  void reclaimStream_withOldMessages_claimOldMessages() {
    final PendingMessage oldMsg = this.buildPendingMessage(Duration.ofSeconds(120));
    final PendingMessages pending = this.buildPendingMessages(List.of(oldMsg));

    when(this.streamOperations.pending(
        anyString(), anyString(), any(Range.class), anyLong()
    )).thenReturn(pending);

    this.reclaimService.reclaimStream(STREAM, GROUP);

    verify(this.streamOperations, times(1)).claim(
        eq(STREAM),
        eq(GROUP),
        anyString(),
        eq(Duration.ofSeconds(60)),
        any(RecordId.class)
    );
  }

  @Test
  void reclaimStream_ignoresRecentMessages() {
    final PendingMessage msg = org.mockito.Mockito.mock(PendingMessage.class);
    when(msg.getElapsedTimeSinceLastDelivery()).thenReturn(Duration.ofSeconds(30));

    final PendingMessages pending = this.buildPendingMessages(List.of(msg));

    when(this.streamOperations.pending(
        anyString(), anyString(), any(Range.class), anyLong()
    )).thenReturn(pending);

    this.reclaimService.reclaimStream(STREAM, GROUP);

    verify(this.streamOperations, never())
        .claim(anyString(), anyString(), anyString(), any(Duration.class), any(RecordId.class));
  }

  @Test
  void testReclaimStreamHandlesExceptionsGracefully() {
    final PendingMessage oldMsg = this.buildPendingMessage(Duration.ofSeconds(120));
    final PendingMessages pending = this.buildPendingMessages(List.of(oldMsg));

    when(this.streamOperations.pending(
        anyString(), anyString(), any(Range.class), anyLong()
    )).thenReturn(pending);

    when(this.streamOperations.claim(
        anyString(), anyString(), anyString(), any(Duration.class), any(RecordId.class)
    )).thenThrow(new RuntimeException("Claim failed"));

    assertThatNoException().isThrownBy(() -> this.reclaimService.reclaimStream(STREAM, GROUP));

    verify(this.streamOperations, times(1))
        .claim(anyString(), anyString(), anyString(), any(Duration.class), any(RecordId.class));
  }

  private PendingMessage buildPendingMessage(Duration elapsed) {
    final PendingMessage msg = org.mockito.Mockito.mock(PendingMessage.class);
    when(msg.getElapsedTimeSinceLastDelivery()).thenReturn(elapsed);
    when(msg.getId()).thenReturn(RecordId.of("1234567890000-0"));
    return msg;
  }

  private PendingMessages buildPendingMessages(List<PendingMessage> messages) {
    final PendingMessages pending = org.mockito.Mockito.mock(PendingMessages.class);
    when(pending.isEmpty()).thenReturn(false);
    when(pending.iterator()).thenReturn(messages.iterator());
    return pending;
  }

  private static class TestReclaimService extends GenericReclaimService {

    TestReclaimService(RedisTemplate<String, Object> redisTemplate) {
      super(redisTemplate);
    }

    @Override
    public void reclaim() {
      this.reclaimStream("test.stream", "test-group");
    }
  }
}
