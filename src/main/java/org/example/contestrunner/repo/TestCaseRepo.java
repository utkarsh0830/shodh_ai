package org.example.contestrunner.repo;

import org.example.contestrunner.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestCaseRepo extends JpaRepository<TestCase, Long> {
    List<TestCase> findByProblemId(Long problemId);
}
