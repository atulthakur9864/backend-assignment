package com.atul.assignment.backend_assignment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class NotificationService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void processBotNotification(Long userId, String message) {
        String cooldownKey = "notif_cooldown:" + userId;
        
        // Check if the user has received a notification in the last 15 minutes [cite: 42]
        String hasRecentNotif = redisTemplate.opsForValue().get(cooldownKey);

        if (hasRecentNotif != null) {
            // If YES: Push message into a Redis List [cite: 43]
            redisTemplate.opsForList().rightPush("user:" + userId + ":pending_notifs", message);
        } else {
            // If NO: Log to console and set 15-minute cooldown [cite: 44]
            System.out.println("Push Notification Sent to User: " + message);
            redisTemplate.opsForValue().set(cooldownKey, "active", 15, TimeUnit.MINUTES);
        }
    }
}