package com.atul.assignment.backend_assignment.service;

import com.atul.assignment.backend_assignment.entity.Post;
import com.atul.assignment.backend_assignment.entity.Comment;
import com.atul.assignment.backend_assignment.repository.PostRepository;
import com.atul.assignment.backend_assignment.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.TimeUnit;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private StringRedisTemplate redisTemplate; // Direct use for Atomic Operations

    @Transactional
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    @Transactional
    public void likePost(Long id, boolean isBot) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        // Virality Score Logic [cite: 23, 25]
        String scoreKey = "post:" + id + ":virality_score";
        if (isBot) {
            redisTemplate.opsForValue().increment(scoreKey, 1); // Bot Like/Reply = +1 [cite: 24]
        } else {
            redisTemplate.opsForValue().increment(scoreKey, 20); // Human Like = +20 [cite: 25]
        }

        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
    }

    @Transactional
    public Comment addComment(Long postId, Comment comment, boolean isBot, Long authorId, Long targetHumanId) {
        // 1. Vertical Cap: Depth Check [cite: 35]
        if (comment.getDepthLevel() > 20) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thread too deep (Max 20)");
        }

        if (isBot) {
            // 2. Horizontal Cap: Atomic Lock [cite: 33]
            String botCountKey = "post:" + postId + ":bot_count";
            Long count = redisTemplate.opsForValue().increment(botCountKey);
            if (count > 100) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Horizontal Cap Exceeded"); // [cite: 34]
            }

            // 3. Cooldown Cap: 10 Minutes [cite: 36, 37]
            String cooldownKey = "cooldown:bot_" + authorId + ":human_" + targetHumanId;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Bot in cooldown"); // [cite: 38]
            }
            redisTemplate.opsForValue().set(cooldownKey, "active", 10, TimeUnit.MINUTES); // [cite: 37]

            // 4. Phase 3: Notification Throttler [cite: 41, 42]
            handleNotification(targetHumanId, "Bot " + authorId + " replied to your post");
        } else {
            // Human Comment Virality [cite: 26]
            redisTemplate.opsForValue().increment("post:" + postId + ":virality_score", 50);
        }

        return commentRepository.save(comment);
    }

    private void handleNotification(Long userId, String message) {
        String lockKey = "notif_cooldown:" + userId;
        String listKey = "user:" + userId + ":pending_notifs";

        if (Boolean.TRUE.equals(redisTemplate.hasKey(lockKey))) {
            // Notification cooldown active, push to list [cite: 43]
            redisTemplate.opsForList().rightPush(listKey, message);
        } else {
            // Send immediate and set 15-min cooldown [cite: 44]
            System.out.println("Push Notification Sent to User: " + userId);
            redisTemplate.opsForValue().set(lockKey, "active", 15, TimeUnit.MINUTES);
        }
    }
}