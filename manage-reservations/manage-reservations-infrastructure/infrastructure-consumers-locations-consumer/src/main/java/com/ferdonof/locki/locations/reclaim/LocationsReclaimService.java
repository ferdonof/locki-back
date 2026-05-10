package com.ferdonof.locki.locations.reclaim;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ferdonof.locki.base.GenericReclaimService;

@Slf4j
@Component
public class LocationsReclaimService extends GenericReclaimService {
  public LocationsReclaimService(RedisTemplate<String, Object> redisTemplate) {
    super(redisTemplate);
  }

  @Scheduled(fixedDelay = 15000)
  @Override
  public void reclaim() {
    this.reclaimStream("locations.events", "locations-group");
  }
}
