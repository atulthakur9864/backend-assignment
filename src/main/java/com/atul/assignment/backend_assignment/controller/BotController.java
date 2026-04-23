package com.atul.assignment.backend_assignment.controller;

import com.atul.assignment.backend_assignment.entity.Bot;
import com.atul.assignment.backend_assignment.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bots")
public class BotController {

    @Autowired
    private BotRepository botRepository;

    @PostMapping("/create")
    public Bot createBot(@RequestBody Bot bot) {
        // Assignment says Postgres is the source of truth
        return botRepository.save(bot);
    }
}