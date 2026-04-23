package com.atul.assignment.backend_assignment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class NotificationSweeper {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // Har 5 minute mein chalega 
    @Scheduled(fixedRate = 300000) 
    public void sweepNotifications() {
        // Saare users ki pending notification lists dhoondo [cite: 47]
        Set<String> keys = redisTemplate.keys("user:*:pending_notifs");

        if (keys != null) {
            for (String key : keys) {
                
                List<String> messages = redisTemplate.opsForList().range(key, 0, -1);
                
                if (messages != null && !messages.isEmpty()) {
                    String userId = key.split(":")[1];
                    int count = messages.size();
                    String firstBot = messages.get(0).split(" ")[1];

                  
                    System.out.println("Summarized Push Notification: Bot " + firstBot + 
                                       " and [" + (count - 1) + "] others interacted with your posts.");

                   
                    redisTemplate.delete(key);
                }
            }
        }
    }
}