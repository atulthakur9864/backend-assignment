package com.atul.assignment.backend_assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.atul.assignment.backend_assignment.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}