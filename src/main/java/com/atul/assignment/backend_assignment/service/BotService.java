package com.atul.assignment.backend_assignment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit; // FIX: Ye import zaroori hai

@Service
public class BotService {

    @Autowired
    private RedisService redisService;

    public String botInteractWithPost(Long postId, Long botId, Long humanAuthorId) {
        
        String cooldownKey = "cooldown:bot_" + botId + ":human_" + humanAuthorId;
        
       
        boolean allowed = redisService.setIfAbsent(cooldownKey, "1", 10, TimeUnit.MINUTES);

        if (!allowed) {
            return "Cooldown active: Cannot interact yet.";
        }

        // 2. Horizontal Cap (Max 100 bots)
        String botCapKey = "post:" + postId + ":bot_count";
        Long currentBotCount = redisService.increment(botCapKey);


        if (currentBotCount != null && currentBotCount > 100) {
            redisService.decrement(botCapKey); // Rollback
            return "429 Too Many Requests: Bot limit for this post reached.";
        }

        // 3. Virality Score: Bot Interaction = +1 Point
        redisService.increment("post:" + postId + ":virality_score");

        return "Bot interaction successful";
    }
}