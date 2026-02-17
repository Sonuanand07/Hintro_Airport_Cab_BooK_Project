package com.hintro.airportpooling.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hintro.airportpooling.dto.RideGroupResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class GroupCacheService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final Duration ttl;

    public GroupCacheService(RedisTemplate<String, String> redisTemplate,
                             ObjectMapper objectMapper,
                             @Value("${redis.cache-ttl-seconds:60}") long ttlSeconds) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.ttl = Duration.ofSeconds(ttlSeconds);
    }

    public Optional<RideGroupResponse> getGroup(Long groupId) {
        String key = key(groupId);
        String payload = redisTemplate.opsForValue().get(key);
        if (payload == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(payload, RideGroupResponse.class));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    public void putGroup(RideGroupResponse response) {
        try {
            String payload = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(key(response.getId()), payload, ttl);
        } catch (JsonProcessingException ignored) {
        }
    }

    public void evictGroup(Long groupId) {
        redisTemplate.delete(key(groupId));
    }

    private String key(Long groupId) {
        return "group:" + groupId;
    }
}
