package org.example.contestrunner.dto;
import jakarta.validation.constraints.*;
public record SubmissionRequest(
        @NotNull Long contestId,
        @NotNull Long problemId,
        @NotBlank String userHandle,
        @NotBlank String language,     // "java"
        @NotBlank String sourceCode
) {}
