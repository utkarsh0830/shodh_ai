package org.example.contestrunner.service;

import org.example.contestrunner.amqp.AmqpConfig;
import org.example.contestrunner.dto.SubmissionRequest;
import org.example.contestrunner.model.*;
import org.example.contestrunner.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SubmissionService {
    private final UserRepo userRepo;
    private final ContestRepo contestRepo;
    private final ProblemRepo problemRepo;
    private final SubmissionRepo submissionRepo;
    private final RabbitTemplate rabbitTemplate;

    public Submission createAndQueue(SubmissionRequest req) {
        User user = userRepo.findByHandle(req.userHandle())
                .orElseGet(() -> userRepo.save(User.builder().handle(req.userHandle()).build()));
        Contest contest = contestRepo.findById(req.contestId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid contestId"));
        Problem problem = problemRepo.findById(req.problemId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid problemId"));

        Submission sub = Submission.builder()
                .user(user)
                .contest(contest)
                .problem(problem)
                .language(req.language().toLowerCase())
                .sourceCode(req.sourceCode())
                .verdict(Verdict.PENDING)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .passedCount(0)
                .totalCount(0)
                .build();

        sub = submissionRepo.save(sub);
        rabbitTemplate.convertAndSend(AmqpConfig.QUEUE_SUBMISSIONS, sub.getId());
        return sub;
    }
}
