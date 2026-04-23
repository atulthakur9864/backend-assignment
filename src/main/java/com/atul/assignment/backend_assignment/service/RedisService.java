package com.atul.assignment.backend_assignment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    
    @Autowired
    private StringRedisTemplate redisTemplate;

    // 1. SETNX (Set if Not Exists) with TTL - Cooldown ke liye Sabse Zaroori
    public boolean setIfAbsent(String key, String value, long duration, TimeUnit unit) {
        // Ye atomic operation hai, concurrency control handle karta hai
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, duration, unit));
    }

    // 2. Increment Counter (Used for Virality Score and Bot Cap)
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    // 3. Increment By (Human interaction ke liye +50)
    public Long incrementBy(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    // 4. Decrement (Agar logic fail ho jaye to rollback ke liye)
    public Long decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    // 5. Standard Get (Validation ke liye)
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    // 6. Delete Key (Agar zaroorat pade)
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}