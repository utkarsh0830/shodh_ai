package org.example.contestrunner.service;



import org.example.contestrunner.amqp.AmqpConfig;
import org.example.contestrunner.model.*;
import org.example.contestrunner.repo.SubmissionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@ConditionalOnProperty(prefix = "app.worker", name = "enabled", havingValue = "true", matchIfMissing = true)
@Component
@RequiredArgsConstructor
public class SubmissionWorker{
    private final SubmissionRepo submissionRepo;
    private final JudgeRunnerService judgeRunner;

    @RabbitListener(queues = AmqpConfig.QUEUE_SUBMISSIONS)
    public void handle(Long submissionId) {
        Submission sub = submissionRepo.findById(submissionId).orElse(null);
        if (sub == null) return;
        try {
            sub.setVerdict(Verdict.RUNNING);
            sub.setUpdatedAt(Instant.now());
            submissionRepo.save(sub);

            var res = judgeRunner.judge(sub);
            sub.setVerdict(res.verdict());
            sub.setMessage(res.message());
            sub.setPassedCount(res.passed());
            sub.setTotalCount(res.total());
            sub.setUpdatedAt(Instant.now());
            submissionRepo.save(sub);
        } catch (Exception e) {
            sub.setVerdict(Verdict.SYSTEM_ERROR);
            sub.setMessage(e.getMessage());
            sub.setUpdatedAt(Instant.now());
            submissionRepo.save(sub);
        }
    }
}

