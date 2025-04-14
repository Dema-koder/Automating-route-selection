package org.example.project.repository;

import org.example.project.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByName(String name);
    Users findByChatId(Long chatId);
    @Query(value = "select * from users", nativeQuery = true)
    List<Users> getAllUsers();
}
