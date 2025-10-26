package org.example.contestrunner.repo;

import org.example.contestrunner.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProblemRepo extends JpaRepository<Problem, Long> {
    Optional<Problem> findByCode(String code);
}