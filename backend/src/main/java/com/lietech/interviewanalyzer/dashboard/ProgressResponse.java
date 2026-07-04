package com.lietech.interviewanalyzer.dashboard;

import java.time.Instant;

public record ProgressResponse(
        Long interviewId,
        String title,
        Integer score,
        Instant completedAt
) {
}