package com.ferdonof.locki.locations.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import com.ferdonof.locki.base.BaseStreamConsumer;

@Configuration
public class LocationsConsumerConfig {

  @Bean
  public StreamMessageListenerContainer<String, MapRecord<String, String, String>> locationsConsumerContainer(
      RedisConnectionFactory factory,
      BaseStreamConsumer consumer) {

    final var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
        .builder()
        .pollTimeout(Duration.ofSeconds(2))
        .build();

    final StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
        StreamMessageListenerContainer.create(factory, options);

    container.receive(
        Consumer.from("locations-service", "consumer-1"),
        StreamOffset.create("locations.events", ReadOffset.lastConsumed()),
        (MapRecord<String, String, String> msg) -> {
          @SuppressWarnings("unchecked")
          final MapRecord<String, Object, Object> mappedMsg =
              (MapRecord<String, Object, Object>) (MapRecord<?, ?, ?>) msg;
          consumer.handle("locations-service", mappedMsg);
        }
    );

    container.start();

    return container;
  }
}


