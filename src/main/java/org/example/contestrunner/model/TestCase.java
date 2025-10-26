package org.example.contestrunner.model;
import jakarta.persistence.*;
import lombok.*;
import org.example.contestrunner.model.Problem;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TestCase {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    @ManyToOne @JoinColumn(name="problem_id")
    Problem problem;
    @Lob String inputData;
    @Lob String expectedOutput;
    boolean sampleCase; // for UI, still judged
}