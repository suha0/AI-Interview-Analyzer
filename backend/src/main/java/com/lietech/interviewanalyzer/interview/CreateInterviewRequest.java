package com.lietech.interviewanalyzer.interview;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateInterviewRequest(

        @NotBlank(message = "title is required")
        String title,

        @NotBlank(message = "targetRole is required")
        String targetRole,

        String company,

        String scheduledAt,

        @NotBlank(message = "difficulty is required")
        String difficulty,

        @NotNull(message = "count is required")
        Integer count

) {
}