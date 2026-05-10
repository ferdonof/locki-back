package com.ferdonof.locki.locations.consumer;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.ferdonof.locki.base.BaseStreamConsumer;
import com.ferdonof.locki.base.DeadLetterService;
import com.ferdonof.locki.base.IdempotencyService;

@Slf4j
@Component
public class LocationsConsumer extends BaseStreamConsumer {

  public LocationsConsumer(RedisTemplate<String, Object> redisTemplate, IdempotencyService idempotencyService, DeadLetterService dlq) {
    super(redisTemplate, idempotencyService, dlq);
  }

  @Override
  protected void handleProcessing(Map<Object, Object> data) {
    log.info("Processing location event: {}", data);
  }
}

