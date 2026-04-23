package com.atul.assignment.backend_assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atul.assignment.backend_assignment.entity.Post;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}