package org.example.project.repository;

import org.example.project.domain.ChatGptMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatGptMessagesRepository extends JpaRepository<ChatGptMessages, Long> {
}
