package com.ferdonof.locki.adapters;

import org.mapstruct.factory.Mappers;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferdonof.locki.mappers.CachedFeeMapper;

@SpringBootApplication
public class TestCacheApplication {

  @Bean
  public FeeCacheAdapter feeCacheAdapter(
      final CachedFeeMapper cachedFeeMapper,
      final StringRedisTemplate redisTemplate) {
    return new FeeCacheAdapter(cachedFeeMapper, redisTemplate, new ObjectMapper());
  }

  @Bean
  public CachedFeeMapper cachedFeeMapper() {
    return Mappers.getMapper(CachedFeeMapper.class);
  }
}

