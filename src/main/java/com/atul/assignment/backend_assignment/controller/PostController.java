package com.atul.assignment.backend_assignment.controller;

import com.atul.assignment.backend_assignment.entity.Post;
import com.atul.assignment.backend_assignment.service.PostService;
import com.atul.assignment.backend_assignment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    // 1. Create Post
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        return ResponseEntity.ok(postService.createPost(post));
    }

    // 2. Like Post 
    @PostMapping("/{postId}/like")
    public ResponseEntity<String> likePost(
            @PathVariable Long postId,
            @RequestParam boolean isBot) {
        postService.likePost(postId, isBot);
        return ResponseEntity.ok("Post liked!");
    }

    // 3. Add Comment 
    @PostMapping("/{postId}/comments")
    public ResponseEntity<String> addComment(
            @PathVariable Long postId,
            @RequestParam Long authorId,
            @RequestParam(required = false) Long humanAuthorId, 
            @RequestParam boolean isBot,
            @RequestParam int depth,
            @RequestParam String text) {
        
        try {
            commentService.addComment(postId, authorId, humanAuthorId, isBot, depth, text);
            return ResponseEntity.ok("Comment added successfully!");
        } catch (RuntimeException e) {
            // Agar bot limit cross ho (100+) toh 429 error code dega
            if (e.getMessage().contains("429")) {
                return ResponseEntity.status(429).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}