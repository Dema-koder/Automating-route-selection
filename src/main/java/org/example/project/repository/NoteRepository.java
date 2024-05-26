package org.example.project.repository;

import org.example.project.domain.Note;
import org.example.project.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUsers(Users user);
}
