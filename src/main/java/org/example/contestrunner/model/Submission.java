package org.example.contestrunner.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(indexes = {
        @Index(columnList="user_id"),
        @Index(columnList="problem_id"),
        @Index(columnList="contest_id")
})
public class Submission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    @ManyToOne @JoinColumn(name="user_id")
    org.example.contestrunner.model.User user;
    @ManyToOne @JoinColumn(name="problem_id") Problem problem;
    @ManyToOne @JoinColumn(name="contest_id") Contest contest;
    String language;             // "java"
    @Lob String sourceCode;
    @Enumerated(EnumType.STRING) Verdict verdict;
    String message;              // error / diagnostics
    Instant createdAt;
    Instant updatedAt;
    Integer passedCount;
    Integer totalCount;
}