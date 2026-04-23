# Social Media Backend Assignment (Grid07)

This project is a Spring Boot application designed to handle social media interactions with advanced guardrails using Redis.

## Tech Stack
* **Java 17 & Spring Boot**
* **PostgreSQL** (Data Persistence)
* **Redis** (Atomic Locks & Rate Limiting)
* **Docker & Docker Compose**

## Features Implemented
* **Phase 2 (Guardrails):** Implemented Atomic Locks using Redis `INCR` and `TTL` to prevent bot spamming.
* **Horizontal Cap:** Max 100 bot comments per post.
* **Cooldown Cap:** 10-minute cooldown for bots interacting with the same user.
* **Phase 3 (Notifications):** Implemented a 15-minute throttling logic and a 5-minute CRON sweeper to batch and summarize notifications.

## How to Run
1. Ensure Docker is running.
2. Run `docker-compose up --build`.
3. The API will be available at `http://localhost:8080`.

## API Endpoints
* **Create Post:** `POST /api/posts` (Body: `{"authorId": 1, "content": "Post Content"}`)
* **Add Comment:** `POST /api/posts/{id}/comments` (Body: `{"authorId": 101, "content": "Bot Comment", "isBot": true, "targetHumanId": 1, "depthLevel": 1}`)
* **Like Post:** `POST /api/posts/{id}/like`