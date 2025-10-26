package org.example.contestrunner.config;

import org.example.contestrunner.model.*;
import org.example.contestrunner.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(ContestRepo contestRepo, ProblemRepo problemRepo, TestCaseRepo tcRepo) {
        return args -> {
            if (contestRepo.count() > 0) return;

            Contest contest = Contest.builder()
                    .name("Sample Contest")
                    .startTime(Instant.now())
                    .endTime(Instant.now())
                    .build();
            contest = contestRepo.save(contest);

            Problem suma = Problem.builder()
                    .contest(contest)
                    .code("SUMA")
                    .title("Simple Sum")
                    .description("Read two integers and print their sum")
                    .score(100)
                    .build();
            suma = problemRepo.save(suma);

            tcRepo.saveAll(List.of(
                    TestCase.builder().problem(suma).inputData("2 3\n").expectedOutput("5").sampleCase(true).build(),
                    TestCase.builder().problem(suma).inputData("100 250\n").expectedOutput("350").sampleCase(false).build()
            ));

            Problem fact = Problem.builder()
                    .contest(contest)
                    .code("FACT")
                    .title("Factorial")
                    .description("Read N and print N!")
                    .score(100)
                    .build();
            fact = problemRepo.save(fact);

            tcRepo.saveAll(List.of(
                    TestCase.builder().problem(fact).inputData("5\n").expectedOutput("120").sampleCase(true).build(),
                    TestCase.builder().problem(fact).inputData("1\n").expectedOutput("1").sampleCase(false).build(),
                    TestCase.builder().problem(fact).inputData("0\n").expectedOutput("1").sampleCase(false).build()
            ));
        };
    }
}
