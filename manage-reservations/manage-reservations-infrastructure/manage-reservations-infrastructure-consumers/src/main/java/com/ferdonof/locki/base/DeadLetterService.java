package com.ferdonof.locki.base;

import java.util.Map;

import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class DeadLetterService {

  private final RedisTemplate<String, Object> redisTemplate;

  public DeadLetterService(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void send(String originalStream, Map<Object, Object> data) {

    final String dlqStream = originalStream + ".dlq";

    this.redisTemplate
        .opsForStream()
        .add(StreamRecords
            .mapBacked(data)
            .withStreamKey(dlqStream));
  }
}