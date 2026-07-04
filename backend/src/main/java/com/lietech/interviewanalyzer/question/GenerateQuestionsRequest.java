package com.lietech.interviewanalyzer.question;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record GenerateQuestionsRequest(
        @NotBlank String difficulty,
        @Min(1) @Max(10) int count
) {
}
