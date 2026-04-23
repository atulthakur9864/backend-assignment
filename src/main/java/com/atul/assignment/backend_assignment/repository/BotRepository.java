package com.atul.assignment.backend_assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.atul.assignment.backend_assignment.entity.Bot;

@Repository
public interface BotRepository extends JpaRepository<Bot, Long> {
    // Agar future mein bot ke naam se search karna ho to ye kaam ayega
}