package com.atul.assignment.backend_assignment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atul.assignment.backend_assignment.entity.Post;
import com.atul.assignment.backend_assignment.entity.Comment;
import com.atul.assignment.backend_assignment.repository.CommentRepository;
import com.atul.assignment.backend_assignment.repository.PostRepository;

import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.TimeUnit; // FIX 1: Ye import missing tha

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private RedisService redisService;

    @Transactional 
    public void addComment(Long postId, Long authorId, Long humanId, boolean isBot, int depth, String text) {

      
        if (depth > 20) {
            throw new RuntimeException("Depth limit exceeded");
        }

        if (isBot) {
     
            String cooldownKey = "cooldown:bot_" + authorId + ":human_" + humanId;

            boolean isNewAction = redisService.setIfAbsent(cooldownKey, "active", 10, TimeUnit.MINUTES);
            
            if (!isNewAction) {
                throw new RuntimeException("Cooldown active: Only one interaction per 10 mins");
            }

          
            String botCountKey = "post:" + postId + ":bot_count";
            Long botCount = redisService.increment(botCountKey);

            if (botCount != null && botCount > 100) {
                redisService.decrement(botCountKey); 
                throw new RuntimeException("429 Too Many Requests");
            }

 
            redisService.increment("post:" + postId + ":virality_score");

        } else {
                redisService.incrementBy("post:" + postId + ":virality_score", 50);
        }

        // 6. Database Save
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        // FIX 3: Make sure these methods exist in your Comment.java
        comment.setContent(text); // Agar entity mein 'text' ki jagah 'content' hai
        comment.setPost(post);
        comment.setAuthorId(authorId);
        comment.setDepthLevel(depth);

        commentRepository.save(comment);
        

        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
    }
}