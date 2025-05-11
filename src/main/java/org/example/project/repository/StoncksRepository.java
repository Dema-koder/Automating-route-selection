package org.example.project.repository;

import org.example.project.domain.Stoncks;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoncksRepository extends JpaRepository<Stoncks, Long> {
}
