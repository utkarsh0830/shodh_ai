package org.example.contestrunner.repo;

import org.example.contestrunner.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepo extends JpaRepository<Submission, Long> {
    List<Submission> findByContestId(Long contestId);
}