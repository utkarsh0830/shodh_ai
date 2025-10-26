package org.example.contestrunner.service;


import org.example.contestrunner.model.*;
import lombok.RequiredArgsConstructor;
import org.example.contestrunner.repo.TestCaseRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JudgeRunnerService {

    private final TestCaseRepo testCaseRepo;

    @Value("${app.judge.image}") String runnerImage;
    @Value("${app.judge.timeLimitMs}") long timeLimitMs;
    @Value("${app.judge.memoryLimitMb}") long memoryLimitMb;
    @Value("${app.judge.cpuLimit}") String cpuLimit;
    @Value("${app.judge.workdirHost}") String workdirHost;
    @Value("${app.judge.dockerBinary}") String dockerBin;

    public record JudgeResult(Verdict verdict, String message, int passed, int total) {}

    public JudgeResult judge(Submission submission) {
        try {
            // 1) prepare temp dir & write code
            String runId = "sub-"+submission.getId()+"-"+UUID.randomUUID();
            Path hostDir = Paths.get(workdirHost, runId);
            Files.createDirectories(hostDir);
            Path codeFile = hostDir.resolve("Main.java");
            Files.writeString(codeFile, submission.getSourceCode(), StandardCharsets.UTF_8);

            List<TestCase> cases = testCaseRepo.findByProblemId(submission.getProblem().getId());
            int passed = 0;

            // 2) docker build is pre-done (we use prebuilt image). For each test: compile once, then run.
            // compile step inside container
            String compileCmd = """
          /bin/sh -lc "javac Main.java 2> compile.err"
          """;
            ExecResult comp = dockerRun(hostDir, compileCmd, null);
            if (comp.exitCode != 0) {
                String err = Files.readString(hostDir.resolve("compile.err"));
                cleanup(hostDir);
                return new JudgeResult(Verdict.COMPILATION_ERROR, truncate(err), 0, cases.size());
            }

            // 3) run per test
            for (TestCase tc : cases) {
                String runCmd = """
            /bin/sh -lc "timeout %dms java Main 2> run.err"
            """.formatted(timeLimitMs);
                ExecResult ex = dockerRun(hostDir, runCmd, tc.getInputData());
                if (ex.timedOut) {
                    cleanup(hostDir);
                    return new JudgeResult(Verdict.TIME_LIMIT_EXCEEDED, "Time limit exceeded", passed, cases.size());
                }
                if (ex.exitCode != 0) {
                    String err = Files.readString(hostDir.resolve("run.err"));
                    cleanup(hostDir);
                    return new JudgeResult(Verdict.RUNTIME_ERROR, truncate(err), passed, cases.size());
                }
                String actual = ex.stdout().replaceAll("\\R+$","");
                String expected = (tc.getExpectedOutput()==null?"":tc.getExpectedOutput()).replaceAll("\\R+$","");
                if (actual.equals(expected)) passed++;
                else {
                    cleanup(hostDir);
                    return new JudgeResult(Verdict.WRONG_ANSWER,
                            "Expected: ["+expected+"], Got: ["+actual+"]", passed, cases.size());
                }
            }
            cleanup(hostDir);
            return new JudgeResult(Verdict.ACCEPTED, "All tests passed", passed, cases.size());
        } catch (Exception e) {
            return new JudgeResult(Verdict.SYSTEM_ERROR, e.getMessage(), 0, 0);
        }
    }

    private record ExecResult(int exitCode, boolean timedOut, String stdout) { }


    private ExecResult dockerRun(Path hostDir, String innerCommand, String stdin) throws Exception {
        // Build docker run command with resource limits
        ProcessBuilder pb = new ProcessBuilder(
                dockerBin, "run", "--rm",
                "--cpus", cpuLimit,
                "--memory", memoryLimitMb+"m",
                "--network", "none",
                "-v", hostDir.toAbsolutePath()+":/work",
                "-w", "/work",
                runnerImage,
                "sh", "-lc", innerCommand
        );
        Process p = pb.start();

        if (stdin != null) {
            try (OutputStream os = p.getOutputStream()) {
                os.write(stdin.getBytes(StandardCharsets.UTF_8));
            }
        } else {
            p.getOutputStream().close();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = pipe(p.getInputStream(), out);
        Thread t2 = pipe(p.getErrorStream(), OutputStream.nullOutputStream());
        boolean finished = p.waitFor( (long)Math.ceil(timeLimitMs/1000.0) + 5, java.util.concurrent.TimeUnit.SECONDS);
        t.join(); t2.join();
        if (!finished) {
            p.destroyForcibly();
            return new ExecResult(124, true, out.toString(StandardCharsets.UTF_8));
        }
        return new ExecResult(p.exitValue(), false, out.toString(StandardCharsets.UTF_8));
    }

    private Thread pipe(InputStream in, OutputStream out) {
        Thread t = new Thread(() -> {
            try (in; out) { in.transferTo(out); } catch (IOException ignored) {}
        });
        t.start();
        return t;
    }

    private void cleanup(Path dir) {
        try { Files.walk(dir).sorted((a,b)->b.getNameCount()-a.getNameCount()).forEach(p-> { try { Files.deleteIfExists(p);} catch(Exception ignored){} }); }
        catch (Exception ignored) {}
    }

    private String truncate(String s) {
        if (s == null) return "";
        return s.length() > 800 ? s.substring(0,800) + "...(truncated)" : s;
    }
}
