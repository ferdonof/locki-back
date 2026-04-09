package adapters;

import entities.CachedFee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mappers.CachedFeeMapper;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferdonof.locki.fee.entities.Fee;
import com.ferdonof.locki.fee.ports.FeeCachePort;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeeCacheAdapter implements FeeCachePort {

  private static final Duration TTL = Duration.ofHours(24);

  private static final String KEY_PREFIX = "fee:%s:%s:%s";

  private final CachedFeeMapper cachedFeeMapper;

  private final StringRedisTemplate redisTemplate;

  private final ObjectMapper objectMapper;

  @Override
  public void put(Fee fee) {
    if (this.cantBuildKey(fee)) {
      log.warn("Cannot cache null fee or fee with null id");
      return;
    }

    final CachedFee cachedFee = this.cachedFeeMapper.toCachedFee(fee);
    final String key = KEY_PREFIX.formatted(cachedFee.country(), cachedFee.currency(), cachedFee.lockerSize());

    try {
      final String payload = this.objectMapper.writeValueAsString(cachedFee);
      this.redisTemplate
          .opsForValue()
          .set(key, payload, TTL);
    } catch (final JsonMappingException e) {
      log.error("Failed to serialize fee with key '{}': {}", key, e.getMessage());
    } catch (final Exception e) {
      log.error("Failed to cache fee with key '{}': {}", key, e.getMessage());
    }
  }

  @Override
  public Optional<Fee> get(Fee fee) {
    final String key = KEY_PREFIX.formatted(fee.country(), fee.currency(), fee.lockerSize());
    final String strFee = this.redisTemplate
        .opsForValue()
        .get(key);

    if (strFee == null || strFee.isEmpty()) {
      return Optional.empty();
    }
    try {
      final CachedFee cachedFee = this.objectMapper.readValue(strFee, CachedFee.class);
      return Optional.of(this.cachedFeeMapper.toDomain(cachedFee));
    } catch (final JsonMappingException e) {
      log.error("Failed to deserialize fee with key '{}': {}", key, e.getMessage());
    } catch (final Exception e) {
      log.error("Failed to get cached fee with key '{}': {}", key, e.getMessage());
    }
    return Optional.empty();
  }

  @Override
  public void evictCache(Fee fee) {
    if (fee != null) {
      this.redisTemplate.delete(KEY_PREFIX.formatted(fee.country(), fee.currency(), fee.lockerSize()));
    }
  }

  private boolean cantBuildKey(Fee fee) {
    return fee == null
        || fee.country() == null
        || fee.currency() == null
        || fee.lockerSize() == null;
  }
}
