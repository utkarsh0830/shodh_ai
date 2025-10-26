package org.example.contestrunner.web;

import org.example.contestrunner.model.*;
import org.example.contestrunner.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController @RequestMapping("/api/contests")
@RequiredArgsConstructor
public class ContestController {
    private final ContestRepo contestRepo;
    private final SubmissionRepo submissionRepo;

    @GetMapping("/{id}")
    public Contest get(@PathVariable Long id) { return contestRepo.findById(id).orElseThrow(); }

    // naive leaderboard: most accepted submissions first; tie-breaker by earliest time
    @GetMapping("/{id}/leaderboard")
    public List<Map<String,Object>> leaderboard(@PathVariable Long id) {
        List<Submission> subs = submissionRepo.findByContestId(id);
        Map<String, Map<String,Object>> byUser = new HashMap<>();
        for (Submission s : subs) {
            String h = s.getUser().getHandle();
            var row = byUser.computeIfAbsent(h, k-> {
                Map<String,Object> m = new HashMap<>();
                m.put("user", h);
                m.put("accepted", 0);
                m.put("lastUpdate", s.getUpdatedAt());
                return m;
            });
            if (s.getVerdict()==Verdict.ACCEPTED) {
                row.put("accepted", ((Integer)row.get("accepted"))+1);
            }
            if (s.getUpdatedAt().isAfter((java.time.Instant)row.get("lastUpdate"))) {
                row.put("lastUpdate", s.getUpdatedAt());
            }
        }
        List<Map<String,Object>> rows = new ArrayList<>(byUser.values());
        rows.sort(Comparator
                .<Map<String,Object>, Integer>comparing(m -> -(Integer)m.getOrDefault("accepted",0))
                .thenComparing(m -> (java.time.Instant)m.get("lastUpdate")));
        return rows;
    }
}