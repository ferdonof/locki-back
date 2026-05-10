package com.ferdonof.locki.locations;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.ferdonof.locki.base.DeadLetterService;
import com.ferdonof.locki.base.IdempotencyService;
import com.ferdonof.locki.locations.consumer.LocationsConsumer;
import com.ferdonof.locki.locations.reclaim.LocationsReclaimService;

@SpringBootApplication
public class TestLocationsConsumerApplication {

  @Bean
  DeadLetterService dlq(RedisTemplate<String, Object> redisTemplate) {
    return new DeadLetterService(redisTemplate);
  }

  @Bean
  IdempotencyService idempotencyService() {
    return new IdempotencyService();
  }

  @Bean
  LocationsConsumer locationsConsumer(RedisTemplate<String, Object> redisTemplate, IdempotencyService service,
      DeadLetterService dlq) {
    return new LocationsConsumer(redisTemplate, service, dlq);
  }

  @Bean
  LocationsReclaimService locationsReclaimService(RedisTemplate<String, Object> redisTemplate) {
    return new LocationsReclaimService(redisTemplate);
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    final RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // String serializer for keys
    final StringRedisSerializer stringSerializer = new StringRedisSerializer();

    // JSON serializer for values
    final GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

    // Set serializers
    template.setKeySerializer(stringSerializer);
    template.setValueSerializer(jsonSerializer);
    template.setHashKeySerializer(stringSerializer);
    template.setHashValueSerializer(jsonSerializer);

    template.afterPropertiesSet();
    return template;
  }
}

