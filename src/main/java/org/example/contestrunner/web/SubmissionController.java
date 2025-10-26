package org.example.contestrunner.web;

import org.example.contestrunner.dto.SubmissionRequest;
import org.example.contestrunner.model.Submission;
import org.example.contestrunner.repo.SubmissionRepo;
import org.example.contestrunner.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {
    private final SubmissionService service;
    private final SubmissionRepo submissionRepo;

    @PostMapping
    public Long submit(@Valid @RequestBody SubmissionRequest req) {
        Submission s = service.createAndQueue(req);
        return s.getId();
    }

    @GetMapping("/{id}")
    public Submission get(@PathVariable Long id) {
        return submissionRepo.findById(id).orElseThrow();
    }
}
