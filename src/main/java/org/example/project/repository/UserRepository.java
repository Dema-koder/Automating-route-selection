package org.example.project.repository;

import org.example.project.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByName(String name);
    Users findByChatId(Long chatId);
}
